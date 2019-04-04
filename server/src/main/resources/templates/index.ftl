<#include "./common/header.ftl">
<style>
    .unit{
        margin:5px;
        border: 1px solid rgba(165, 178, 194, 0.35);
        border-radius:10px;
        box-shadow:  0px 0px 10px rgba(0,0,0,0.2);
        oveflow:hidden;

    }

    .thing-img{
            width:150px;
            height:150px;
            float:left;
        margin-right:10px;
        }


    .unit-button
    {
        bottom: 0px;
        margin-bottom:20px;
        position: absolute;
        /*box-shadow:0px 4px rgba(0,0,90,0.5);*/
        border: 1px solid black;
        padding: .4em .6em;
        color:black;
    }

    .unit-button:hover{
        text-decoration: none;
        background-color: black;
        color:ghostwhite;
        transition: all 1s;
    }

    .description{
        width: 400px;
    }

    .card-body{

        position: relative;
    }

</style>
<body>
<#include "./common/navbar.ftl"/>
<div class="container-fluid" id="container-main">
<#--<#if user??>-->
    <#--<#include "./common/sidenavbar.ftl"/>-->
<#--</#if>-->
    <main role="main" class="main pt-3 pb-5 " >
    <#if user??>
        <div class="d-flex flex-wrap  ">
            <#if !units?has_content>
                <div class="alert alert-info">
                    You do not have any Units assigned. Please talk to your administrator.
                </div>
            </#if>
            <#list units as unit>
                <#--<div class="col-md-4">
                    <div class="card">
                        <div class="card-header">${unit.unitName}</div>
                        <div class="card-body">
                            <img height=150 src="<#if (unit.photo)??>${unit.photo}<#else>http://via.placeholder.com/300</#if>" class="float-left p-1">
                            <#if (unit.description)??><p>${unit.description}</p></#if>
                            <div class="clear"></div>
                        </div>
                        <div class="card-footer">
                            <a class="btn btn-primary btn-sm" href="/units/get/${unit.id}"><i class="fa fa-cog"></i>MANAGE</a>
                        </div>
                    </div>
                </div>-->
                <div class="flex-fill ">
                    <div class="card unit">
                        <div class="card-body " >
                            <div ><img class="thing-img" src="/static/img/greenhouse1.jpg" ></div>
                            <span>
                                <div class="description"> <#if (unit.description)??><p>${unit.description}</p></#if> </div>
                            </span>
                            <a class=" unit-button" href="/units/get/${unit.id}" >EXPLORE</a>
                        </div>
                    </div>
                </div>



            </#list>
        </div>

    <#else>

    </#if>
    </main>
</div>

<script src="https://cdnjs.cloudflare.com/ajax/libs/jquery-cookie/1.4.1/jquery.cookie.js"></script>
<script src="/static/js/app.js"></script>

</body>
</html>
