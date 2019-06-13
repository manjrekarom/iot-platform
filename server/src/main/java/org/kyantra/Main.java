package org.kyantra;

import io.swagger.jaxrs.config.BeanConfig;
import org.apache.commons.cli.*;

import org.glassfish.grizzly.http.server.CLStaticHttpHandler;
import org.glassfish.grizzly.http.server.HttpServer;
import org.glassfish.grizzly.http.server.ServerConfiguration;
import org.glassfish.grizzly.ssl.SSLContextConfigurator;
import org.glassfish.grizzly.ssl.SSLEngineConfigurator;
import org.glassfish.jersey.grizzly2.httpserver.GrizzlyHttpServerFactory;
import org.glassfish.jersey.jackson.JacksonFeature;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.server.mvc.freemarker.FreemarkerMvcFeature;

import java.net.URI;
import java.net.URISyntaxException;

import org.kyantra.filters.AuthorizationFilter;
import org.kyantra.filters.CORSFilter;
import org.kyantra.filters.SessionFilter;
import org.kyantra.exception.AppExceptionMapper;
import org.kyantra.core.auth.AuthResource;
import org.kyantra.services.HibernateService;
import org.kyantra.filters.HTTPSRedirectFilter;

public class Main {

    public static HttpServer startServer(int port, boolean useSSL) throws URISyntaxException {

        String[] resources = {"org.kyantra.resources", "org.kyantra.core"};

        BeanConfig beanConfig = new BeanConfig();
        beanConfig.setTitle("E-Yantra IoT Platform API");
        beanConfig.setDescription("Below are endpoints defined for e-Yantra IoT Platform. Note that you need to" +
                "have an account so that you can get the token which is mandatory to make requests.");
        beanConfig.setVersion("1.2.3");
        beanConfig.setSchemes(new String[]{"http"});
        beanConfig.setBasePath("/");
        beanConfig.setResourcePackage(resources[0]);
        beanConfig.setScan(true);

        final ResourceConfig rc = new ResourceConfig().packages(resources);
        rc.property(FreemarkerMvcFeature.TEMPLATE_BASE_PATH,"/templates");
        rc.register(FreemarkerMvcFeature.class);
        rc.register(io.swagger.jaxrs.listing.ApiListingResource.class);
        rc.register(io.swagger.jaxrs.listing.SwaggerSerializers.class);
        rc.register(JacksonFeature.class);
        rc.register(AppExceptionMapper.class);
        rc.register(SessionFilter.class);
        rc.register(AuthorizationFilter.class);
        rc.register(CORSFilter.class);
        rc.register(AuthResource.class);

        ClassLoader loader = Main.class.getClassLoader();
        CLStaticHttpHandler docsHandler = new CLStaticHttpHandler(loader, "swagger-ui/");
        CLStaticHttpHandler staticHttpHandler = new CLStaticHttpHandler(loader,"static/");
        docsHandler.setFileCacheEnabled(false);
        staticHttpHandler.setFileCacheEnabled(true);

        HttpServer server = null;

        if (useSSL) {
            rc.register(HTTPSRedirectFilter.class);

            SSLContextConfigurator sslCon = new SSLContextConfigurator();
            sslCon.setKeyStoreFile("/var/www/intg/intg.ks"); // contains server keypair
            sslCon.setKeyStorePass("intg.io");
            server = GrizzlyHttpServerFactory.createHttpServer(new URI("https://0.0.0.0/"),
                    rc,
                    true,
                    new SSLEngineConfigurator(sslCon).setClientMode(false).setNeedClientAuth(false));

            GrizzlyHttpServerFactory.createHttpServer(URI.create("http://0.0.0.0:80/"), rc);
        } else {
            server = GrizzlyHttpServerFactory.createHttpServer(URI.create("http://0.0.0.0:" + port + "/"), rc);
        }

        ServerConfiguration cfg = server.getServerConfiguration();
        cfg.addHttpHandler(docsHandler, "/docs/");
        // TODO: 5/30/18 Enter to submit form in modals and other places
        cfg.addHttpHandler(staticHttpHandler,"/static/");
        return server;
    }

    public static void main(String[] args) throws ParseException, URISyntaxException {
        Options options = new Options();
        options.addOption("port", true, "Port to run on");
        options.addOption("ssl",false, "Use ssl");

        CommandLineParser parser = new DefaultParser();
        CommandLine cmd = parser.parse( options, args);

        int port = Integer.parseInt(cmd.getOptionValue("port","8002"));
        String env = cmd.getOptionValue("env","dev");

        HibernateService hibernateService = HibernateService.getInstance(); //initialized hibernate service
        final HttpServer server = startServer(port, cmd.hasOption("ssl"));
    }
}
