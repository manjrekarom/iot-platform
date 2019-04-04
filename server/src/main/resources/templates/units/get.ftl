<#include "../common/header.ftl">

<style>

    #plus-btn {
        width: 70px;
        height: 70px;
        background-color: #811a74;
        border-radius: 50%;
        box-shadow: 0 3px 4px 0 #666;
        transition: all 0.3s ease-in-out;
        z-index: 2;
        font-size: 30px;
        color: white;
        text-align: center;
        line-height: 70px;
        position: fixed;
        right: 40px;
        bottom: 40px;
    }

    #plus-btn:hover {
        box-shadow: 0 3px 4px 0 #666;
        text-decoration: none;
        cursor:pointer;
    }


    /*#plus-btn:active{*/
        /*transform: rotate(-135deg);*/
        /*transition-duration: 0.3s;*/
    /*}*/

    #option-tray{
        color:black;
        position: fixed;
        right: 50px;
        bottom: 140px;
        display:none;
    }

    #option-tray div {
        width: 100px;
        background-color: #ffffff;
        margin-top: 20px;
        box-shadow: 0 2px 6px 0 #666;
        padding: 5px;
        border-radius: 5px;
        text-align: center;
        font-size:14px;
    }


    #option-tray div span{
        color:black;
    }

    #option-tray div span:hover{
        text-decoration: none;
        cursor:pointer;
    }

    .animateToCross{
        background-color: #dc3545 !important;
        transform: rotate(135deg);
        transition-duration: 0.3s;
        transition: all 0.3s ease-in-out;
    }
    .animateToPlus{

        transform: rotate(-360deg);
        transition: all 0.3s ease-in-out;
    }
    #breadcrumb{
        font-size: 18px;
        border-radius: 2px;
        background-color: #90b7ea4d;
        margin-top:10px;
        margin-bottom: 40px;

    }
    #arrow{
        color: rgba(0, 0, 0, 0.5);
        font-size:20px;
    }
    #cur_unitName{
        color: black;
        padding:10px;
        margin:2px;
        font-weight: 500;
    }
    .breadCrumbUnit{
        color: rgba(0, 0, 0, 0.5);
        padding:10px;
        margin:2px;
    }
    .breadCrumbUnit:hover{
        color:black;
        text-decoration: none;
    }
    .tile{
        background-color: white;
        width:95%;
        height: 60px;
        padding:10px;
        margin:15px;
        box-shadow: 0px 0px 3px #00000052;
        font-size:21px;

    }

    .btn-delete{
        background-color: #dc3545;

    }
    .btn-edit{
        background-color: #3a5eb3;

    ;
    }
    .mybutton{
        width:40px;
        height:40px;
        border-radius: 50%;
        text-align:center;
        color:white !important;
        box-shadow:0 3px 4px 0 #666 ;
        float:right;
        font-size:25px;
    }
    .mybutton:hover {
        box-shadow: 0 3px 4px 0 #666;
        transform: scale(1.05);


    }

</style>
<body>
<#include "../common/navbar.ftl"/>

<div class="container-fluid" id="container-main">
<#--<#if user??>
    <#include "../common/sidenavbar.ftl"/>
</#if>-->

<main role="main" class="main   pt-3">

   <#-- <div class="row">
        <div class="col-md-12">
            <div class="float-right p-1" v-if="role=='ALL' || role=='WRITE'">
                <button v-on:click="newUnit" class="btn btn-outline-primary">Create Subunit</button>
                <button v-on:click="newThing" class="btn btn-outline-primary">Create Thing</button>
                <button v-on:click="importUnit" class="btn btn-outline-primary">Import</button>
                <button v-on:click="exportUnit" class="btn btn-outline-primary">Export</button>
                <button v-on:click="newUser" class="btn btn-outline-primary">Add User</button>
            </div>
        </div>
        <div class="clearfix"></div>
    </div>-->

    <div class="container">
        <div id="breadcrumb" >
            <span>
            <a href="/"><i class="fa fa-home  breadCrumbUnit" style=" font-size:25px;"aria-hidden="true" ></i></a>
            <i style="padding-left:10px;"class="fa fa-angle-right" id="arrow"></i>
            </span>
       <span v-for="parent in parents" >
           <a  v-bind:href="'/units/get/'+parent.id" class="breadCrumbUnit">{{parent.unitName}}  </a> <i class="fa fa-angle-right" id="arrow"></i>
       </span>
            <span id="cur_unitName">{{ unit.unitName }}</span>
        </div>
        <div class="row">

            <div class="col-md-6">
                <div class="row">
                    <div class="col-md-12">
                        <div class="card shadow-lg" >
                            <div class="card-header">
                                {{ unit.unitName }} <span class="badge badge-success">{{role}}</span>
                            </div>
                            <div class="card-body">
                                <img height=150 v-bind:src="unit.photo || ''" class="float-left p-1"><p>{{unit.description || ''}}</p>
                                <div class="clear"></div><br>

                                <#--     <#list parents as parent>
                                         <p>${parent['id']}</p>
                                     </#list>-->

                            </div>
                            <div class="card-footer">
                                <button v-on:click="edit" class="btn btn-primary btn-sm float-right">EDIT</button>
                                <button v-on:click="deleteUnit" class="btn btn-danger btn-sm float-left text-white">DELETE</button>
                            </div>
                        </div>
                    </div>
                </div>
                <div class="row">
                    <div class="col-md-12">
                        <p>&nbsp;</p>
                        <div class="card" >
                            <div class="card-header">
                                Things
                            </div>
                            <div class="card-body p-0">
                            <span v-if="things.length==0">
                                No Things for this Unit.
                            </span>
                                <#--<table v-if="things.length>0" class="table table-striped">
                                    <tr><th>Name</th><th>Actions</th></tr>
                                    <tr v-for="u in things">
                                        <td><a v-bind:href="'/things/get/'+u.id">{{u.name}}</a></td>
                                        <td><button v-on:click="editThing(u)" class="btn btn-default btn-sm">EDIT</button> </td>
                                    </tr>
                                </table>-->

                                <div class="tile" v-for="u in things">
                                    <a v-bind:href="'/things/get/'+u.id">{{u.name}}</a>
                                    <a v-on:click="editThing(u)" class="btn-edit mybutton" style="margin-left:5px; ">
                                        <i class="fa fa-pencil" aria-hidden="true"></i></a>
                                    <a v-on:click="deleteThing(u)" class="btn-delete mybutton">
                                        <i class="fa fa-trash "  aria-hidden="true"></i></a>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>
                <div class="row">
                    <div class="col-md-12">
                        <p>&nbsp;</p>
                        <div class="card" >
                            <div class="card-header">
                                Subunits
                            </div>
                            <div class="card-body">
                            <span v-if="subunits.length==0">
                                No Subunits for this Unit.
                            </span>

                                <div v-if="subunits.length>0">
                                    <div class="tile" v-for="u in subunits">
                                        <a v-bind:href="'/units/get/'+u.id">{{u.unitName}}</a>
                                    </div>
                                </div>

                            </div>
                        </div>
                    </div>
                </div>

            </div>
            <div class="col-md-6">
                <div class="card" >
                    <div class="card-header">
                        Authorized Users
                    </div>
                    <div class="card-body p-0">
                        <table class="table table-striped">
                            <tr><th>ID</th><th>Name</th><th>Email</th><th>Role</th><th>Actions</th></tr>
                            <tr v-for="right in rights"><td>{{right.user.id}}</td><td>
                                    {{right.user.name}}</td>
                                <td>{{right.user.email}}</td>
                                <td>{{right.role}}</td>
                                <td><button v-on:click="editUser(right)" class="btn btn-default btn-sm">EDIT</button>
                                    <button v-on:click="deleteUser(right)" class="btn btn-default btn-sm">DELETE</button> </td>
                            </tr>
                        </table>
                    </div>
                </div>
            </div>


        </div>
    </div>

</main>


<#include "../modals/crud_unit.ftl"/>
<#include "../modals/crud_thing.ftl"/>
<#include "../modals/crud_user.ftl"/>




    <div id="plus-btn" > +
    </div>


    <div id ="option-tray">
        <div>
            <span v-on:click="newThing" >Create Thing</span>
        </div>
        <div>
            <span v-on:click="newUnit">Create Subunit</span>
        </div>
        <div>
            <span  v-on:click="newUser">Add user</span>
        </div>
        <div>
            <span v-on:click="importUnit" >Import</span>
        </div>
        <div>
            <span v-on:click="exportUnit" >Export</span>
        </div>
    </div>

</div>




<script src="https://cdnjs.cloudflare.com/ajax/libs/jquery-cookie/1.4.1/jquery.cookie.js"></script>
<script src="/static/js/app.js"></script>
<script>
    var token = $.cookie("authorization");
    var userId = ${user.id};
    var unitId = ${unit.id};
    var app = new Vue({
        el: '#container-main',
        data: {
            unit:{},
            role:"",
            rights:[],
            createUnit:{},
            createThing:{},
            createUser:{
                "rights":[]
            },
            saveLoader:false,
            subunits:[],
            things:[],
            parents:[]
            // deleteUnit:{}
        },
        methods: {
            "load": function () {

                var that = this;
                $.ajax({
                    url: "/unit/rights/" + unitId + "/" + userId,
                    success: function (data) {
                        that.role = data[0].role;
                    }
                });

                $.ajax({
                    url: "/unit/get/" + unitId,
                    success: function (data) {
                        that.unit = data;
                    }
                });


                $.ajax({
                    url: "/unit/users/" + unitId,
                    success: function (data) {
                        that.rights = data;
                    }
                });

                $.ajax({
                    url: "/unit/subunits/" + unitId,
                    success: function (data) {
                        data.sort(function (a,b) {
                           return a.unitName < b.unitName ? -1 : 1;
                        });
                        that.subunits = data;
                    }
                });
                $.ajax({
                    url: "/thing/unit/" + unitId,
                    success: function (data) {
                        that.things = data;
                    }
                });
                $.ajax({
                    url: "/unit/parent/" + unitId,
                    success: function (data) {
                        that.parents = data;
                    }
                });

            },
            "newUnit": function () {
                this.createUnit = {
                    "parentUnit_id": unitId
                };
                $("#create_unit").modal('show')
            },
            "newThing": function () {
                this.createThing = {
                    "parentUnitId": unitId
                };
                $("#create_thing").modal('show')
            },
            "editThing": function (thing) {
                this.createThing = thing;
                $("#create_thing").modal('show')
            },
            "deleteThing": function (thing) {
                // alert(thingId);
                if (confirm("Are you sure you want to delete thing?") && confirm("Are you really sure?")) {
                    var that=this;
                   $.ajax({
                        url: "/thing/delete/" + thing.id,
                        "method": "DELETE",
                        success: function (data) {
                            if(!!data.success == false)
                                alert('Could not delete thing :(');
                            else
                                alert('Thing deleted :)');
                            that.load();
                        },
                        error: function(xhr, status, error){
                            alert("Could not delete the thing");
                            //alert(xhr.responseText);
                        }

                    });
                }
            },

            "newUser": function () {
                this.createUser = {
                    "unitId": unitId,
                    "userId": 0
                };
                $("#create_user").modal('show')
            },
            "exportUnit": function () {
                alert(JSON.stringify(this.unit));
            },
            "importUnit": function () {
                //TODO
            },
            "editUser": function (user) {
                this.createUser = user;
                $("#create_user").modal('show')
            },
            "deleteUser": function (right) {
                var that = this;

                if (confirm("Are you sure you want to delete rights of the user for this unit?") && confirm("Are you really sure?")) {
                    /*$.ajax({
                        url: "/user/delete/" + right.user.id,
                        "method": "DELETE",
                        success: function (data) {
                            alert('User deleted');
                            that.deleteRight(right.id);
                            that.load();
                        }
                    });*/

                    $.ajax({
                        url: "/right/delete/" + right.id,
                        "method": "DELETE",
                        success: function (data) {
                            alert('rights deleted');
                            that.load();
                        }
                    });
                }
            },
            "deleteRight": function (rightId) {
                alert(rightId);
                $.ajax({
                    url: "/right/delete/" + rightId,
                    "method": "DELETE",
                    success: function (data) {
                        alert('rights deleted');
                        this.load();
                    }
                });
            },
            "edit": function () {
                this.createUnit = this.unit;
                $("#create_unit").modal('show')
            },
            "saveThing": function () {
                this.saveLoader = true;
                var that = this;
                if (!this.createThing.id) {
                    that.createThing.parentUnitId = unitId;
                    $.ajax({
                        "url": "/thing/create",
                        "method": "POST",
                        "data": that.createThing,
                        "success": function (data) {
                            that.saveLoader = false;
                            $("#create_thing").modal('hide');
                            that.load();
                        }
                    });
                } else {
                    $.ajax({
                        "url": "/thing/update/" + that.createThing.id,
                        "method": "PUT",
                        "data": that.createThing,
                        "success": function (data) {
                            that.saveLoader = false;
                            $("#create_thing").modal('hide');
                            that.load();
                        }

                    });
                }
            },
            "saveUnit": function () {
                this.saveLoader = true;
                var that = this;
                if (!this.createUnit.id) {
                    that.createUnit.parentUnitId = unitId;
                    $.ajax({
                        "url": "/unit/create",
                        "method": "POST",
                        "data": that.createUnit,
                        "success": function (data) {
                            that.saveLoader = false;
                            $("#create_unit").modal('hide');
                            that.unit = data;
                            Vue.set(that.unit, 'id', data.id);
                            Vue.set(that.unit, 'description', data.description);
                            Vue.set(that.unit, 'unitName', data.unitName);
                            Vue.set(that.unit, 'photo', data.photo);
                            that.load();
                        }
                    });
                } else {
                    $.ajax({
                        "url": "/unit/update/" + unitId,
                        "method": "PUT",
                        "data": that.createUnit,
                        "success": function (data) {
                            that.saveLoader = false;
                            $("#create_unit").modal('hide');
                            that.load();
                        }
                    });
                }
            },
            "saveUser": function () {
                this.saveLoader = true;
                var that = this;
                if (!this.createUser.id) {
                    //
                    $.ajax({
                        "url": "/user/create",
                        "method": "POST",
                        "data": that.createUser,
                        "success": function (data) {
                            that.saveLoader = false;
                            $("#create_user").modal('hide');
                            that.createUser.userId = data.id;
                            that.saveRights(that.createUser);
                            that.load();
                        }
                    });
                } else {
                    $.ajax({
                        "url": "/user/update/" + userId,
                        "method": "PUT",
                        "data": that.createUser,
                        "success": function (data) {
                            that.saveLoader = false;
                            $("#create_user").modal('hide');
                            that.createUser.userId = data.id;
                            that.saveRights(that.createUser);
                            that.load();
                        }
                    });
                }
            },
            "saveRights": function (attributes) {

                var that = this;
                $.ajax({
                    "url": "/right/create",
                    "method": "POST",
                    "data": attributes,
                    //contentType: "application/json; charset=utf-8",
                    "success": function (data) {
                        that.saveLoader = false;
                        that.load();
                    }
                });
            },
            "deleteUnit": function () {
                if (confirm("Are you sure you want to delete unit?") && confirm("Are you really sure?")) {
                    $.ajax({
                        url: "/unit/delete/" + unitId,
                        "method": "DELETE",
                        success: function (data) {
                            alert('Unit deleted');
                            this.load();
                        }
                    });
                }
            }
        },
        mounted:function(){
            this.load()
        }
    });
</script>
<script>
    $(document).ready(function () {


        $('#plus-btn').click(function(){
           //$('#option-tray').show()
            $("#option-tray").animate({
                height: 'toggle'
            });

            if ($(this).hasClass('animateToCross')) {
                $(this).removeClass('animateToCross').addClass('animateToPlus');

            } else if ($(this).hasClass('animateToPlus')) {
                $(this).removeClass('animateToPlus').addClass('animateToCross');

            } else {
                $(this).addClass('animateToCross');
            }
        });


    });
</script>

</body>
</html>
