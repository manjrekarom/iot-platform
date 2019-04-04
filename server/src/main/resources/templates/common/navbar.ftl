<header>
    <#--<nav class="navbar navbar-expand-lg navbar-dark bg-dark fixed-top">-->
        <#--<a style="color:#ffffff !important;"  class="navbar-brand" href="/">IoT Platform</a>-->
        <#--<div class="collapse navbar-collapse" id="navbarText">-->
            <#--<ul class="ml-sm-auto, mx-sm-auto"></ul>-->
            <#--<span class="navbar-text text-white clickSlide ">-->
                <#--<#if user??>-->
                   <#--<a href="#" id ="user_link"><span>${user.name}  <span>&#x25BC;</span></span></a>-->
                    <#--<ul id = "pop">-->
                        <#--<li style="text-align: left;"><a class="nav-link <#if active?? && active=="profile">active</#if>" href="/profile">My Profile<i style="float: right;"  class="fa fa-user fa-lg" aria-hidden="true"></i></a></li>-->
                        <#--<li><a class="nav-link text-red" href="/logout">Logout <i style="float: right;" class="fa fa-sign-out fa-lg" aria-hidden="true"></i></a></li>-->
                    <#--</ul>-->
                <#--</#if>-->
            <#--</span>-->
        <#--</div>-->
    <#--</nav>-->


    <nav class="navbar navbar-expand-lg navbar-dark bg-dark fixed-top" id="navigation_bar">
        <a style="color:#ffffff !important;"  class="navbar-brand mr-auto" href="/">IoT Platform</a>
        <a class="nav-link text-white" href="/" id="home-btn"><i style="float: right;" class="fa fa-home fa-lg" aria-hidden="true"></i></a>
        <ul class="my-auto" id="user_bar">
            <li class="nav-item clickSlide" style="list-style-type: none;">
                <a href="#" class="text-white " id ="user_link"><span>${user.name}  <span style="padding-left:5px;">&#x25BC;</span></span></a>
                <ul id = "pop">
                    <li><a class="nav-link text-white <#if active?? && active=="profile">active</#if>" href="/profile"><i class="fa fa-user fa-lg" aria-hidden="true"></i> &nbsp;Profile</a></li>
                    <li><a class="nav-link text-white" href="/logout"><i class="fa fa-sign-out fa-lg" aria-hidden="true"></i> &nbsp;Logout </a></li>
                </ul>
            </li>
        </ul>
    </nav>

    <style>

        #user_link:hover{ text-decoration: none;
            }
        ul{
            padding-left:0px;
        }
        #user_bar{
            width:150px;

        }
            #pop{
                position:absolute;
                background-color: #212529;
                list-style-type: none;
                width:120px;
                margin-top: 15px;
                padding-top:10px;
                border-bottom-left-radius: 5px;
                border-bottom-right-radius: 5px;
            }
        #pop li{
            padding-bottom:20px;
            color:white;
            border-bottom-left-radius: 5px;
            border-bottom-right-radius: 5px;
        }

        #home-btn{

            font-size:20px;
            padding: 12px 10px 12px 10px;
            margin-right:20px;

            color:white;
        }
        #navigation_bar{
        box-shadow: 0px 2px 3px black;
        }

    </style>
</header>

<script>
    $(document).ready(function () {
        $(".clickSlide ul").hide();

        $('.clickSlide ').hover(function(){
            $('#pop').stop(true,false,true).slideToggle(400);
        });






    });
</script>