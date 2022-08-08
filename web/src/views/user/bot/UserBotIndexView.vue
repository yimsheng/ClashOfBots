<template>
<div class="container">
    <div class="row">
        <div class="col-3">
            <div class="card" style="margin-top:20px;">
                <div class="card-body">
                 <img :src="$store.state.user.photo" alt="" style="width:100%;">
                </div>
            </div>
        </div>
        <div class="col-9">
            <div class="card" style="margin-top:20px;">
                <div class="card-header">
                 <span style='font-size:120%'>My bot</span>
                 <button type="button" class="btn btn-primary float-end" data-bs-toggle="modal" data-bs-target="#addbotbutton">
                     Creat Bot
                 </button>
                
                

                <!-- Modal -->
                <div class="modal fade" id="addbotbutton" tabindex="-1">
                <div class="modal-dialog modal-xl">
                    <div class="modal-content">
                    <div class="modal-header">
                        <h5 class="modal-title" id="exampleModalLabel">Create Bot</h5>
                        <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
                    </div>
                    <div class="modal-body">
                            <div class="mb-3">
                                <label for="add-bot-title" class="form-label">Name</label>
                                <input v-model="botadd.title" type="text" class="form-control" id="add-bot-title" placeholder="Please enter name...">
                                
                            </div>
                            <div class="mb-3">
                                <label for="add-bot-description" class="form-label">Description</label>
                                <textarea v-model="botadd.description" class="form-control" id="add-bot-description" rows="3" placeholder="Please enter description..."></textarea>
                                
                            </div>

                            <div class="mb-3">
                                <label for="add-bot-code" class="form-label">Code</label>
                                <VAceEditor
                                        v-model:value="botadd.content"
                                        @init="editorInit"
                                        lang="c_cpp"
                                        theme="textmate"
                                        style="height: 300px" />
                                
                            </div>

                    </div>
                    <div class="modal-footer">
                        <div class="error-message">
                                    {{botadd.error_message}}
                         </div>
                        <button type="button" class="btn btn-primary" @click="add_bot">Save changes</button>
                        <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Close</button>
                        
                    </div>
                    </div>
                </div>
                </div>
                

                </div>
                <div class="card-body">
                    <table class="table table-striped table-hover">
                        <thead>
                            <tr>
                                <th>Name</th>
                                <th>Create At</th>
                                <th>Operations</th>
                            </tr>
                        </thead>
                        <tbody>
                            <!-- 每一行绑定不同的id -->
                            <tr v-for="bot in bots" :key="bot.id">
                                <td>{{bot.title}}</td>
                                <td>{{bot.createtime}}</td>
                                <td>
                                    <button type="button" class="btn btn-secondary" style="margin-right:10px;" data-bs-toggle="modal" :data-bs-target="'#updatebotbutton'+bot.id">Edit</button>
                                    <button type="button" class="btn btn-danger" @click="remove_bot(bot)">Delete</button>

                                                        <!-- Modal -->
                                    <div class="modal fade" :id="'updatebotbutton'+bot.id" tabindex="-1">
                                    <div class="modal-dialog modal-xl">
                                        <div class="modal-content">
                                        <div class="modal-header">
                                            <h5 class="modal-title">Edit Bot</h5>
                                            <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
                                        </div>
                                        <div class="modal-body">
                                                <div class="mb-3">
                                                    <label for="add-bot-title" class="form-label">Name</label>
                                                    <input v-model="bot.title" type="text" class="form-control" id="add-bot-title" placeholder="Please enter name...">
                                                    
                                                </div>
                                                <div class="mb-3">
                                                    <label for="add-bot-description" class="form-label">Description</label>
                                                    <textarea v-model="bot.description" class="form-control" id="add-bot-description" rows="3" placeholder="Please enter description..."></textarea>
                                                    
                                                </div>

                                                <div class="mb-3">
                                                    <label for="add-bot-code" class="form-label">Code</label>
                                                    <VAceEditor
                                                        v-model:value="bot.content"
                                                        @init="editorInit"
                                                        lang="c_cpp"
                                                        theme="textmate"
                                                        style="height: 300px" />
                                                    
                                                </div>

                                        </div>
                                        <div class="modal-footer">
                                            <div class="error-message">
                                                        {{bot.error_message}}
                                            </div>
                                            <button type="button" class="btn btn-primary" @click="update_bot(bot)">Save changes</button>
                                            <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Close</button>
                                            
                                        </div>
                                        </div>
                                    </div>
                                    </div>
                                    
                                </td>
                            </tr>
                        </tbody>
                    </table>
                </div>
            </div>
        </div>
    </div>
</div>
</template>

<script>

import {ref,reactive} from 'vue'
import $ from 'jquery'
import {useStore} from 'vuex'
import {Modal} from 'bootstrap/dist/js/bootstrap'
import { VAceEditor } from 'vue3-ace-editor';
import ace from 'ace-builds';

export default{
    components:{
        VAceEditor
    },
    setup(){
        ace.config.set(
            "basePath", 
            "https://cdn.jsdelivr.net/npm/ace-builds@" + require('ace-builds').version + "/src-noconflict/"
            )

        const store = useStore();
        let bots=ref([]);
        const botadd=reactive({
            title:"",
            description:"",
            content:"",
            error_message:"",
        });
        const refresh_bots = () =>{
            $.ajax({
                url:"http://127.0.0.1:3000/user/bot/getlist/",
                type:"get",
                headers:{
                    Authorization:"Bearer "+store.state.user.token,
                },
                success(resp){
                    bots.value=resp;//后端直接返回列表
                }
            })
        }
        refresh_bots();

        const add_bot=()=>{
            botadd.error_message="";
            $.ajax({
                url:"http://127.0.0.1:3000/user/bot/add/",
                type:"post",
                data:{
                    title: botadd.title,
                    description: botadd.description,
                    content: botadd.content,
                },
                // 需要登录之后才能看的都要加
                headers:{
                    Authorization:"Bearer "+store.state.user.token,
                },
                success(resp){
                    if(resp.error_message==="success"){
                        botadd.title="";
                        botadd.description="";
                        botadd.content="";
                        Modal.getInstance("#addbotbutton").hide();

                        refresh_bots();
                    }else{
                        botadd.error_message=resp.error_message;
                    }
                }
            })
        }


        const update_bot=(bot)=>{
            botadd.error_message="";
            $.ajax({
                url:"http://127.0.0.1:3000/user/bot/update/",
                type:"post",
                data:{
                    bot_id: bot.id,
                    title: bot.title,
                    description: bot.description,
                    content: bot.content,
                },
                // 需要登录之后才能看的都要加
                headers:{
                    Authorization:"Bearer "+store.state.user.token,
                },
                success(resp){
                    if(resp.error_message==="success"){
    
                        Modal.getInstance("#updatebotbutton"+bot.id).hide();

                        refresh_bots();
                    }else{
                        botadd.error_message=resp.error_message;
                    }
                }
            })
        }

        const remove_bot=(bot)=>{
             $.ajax({
                url:"http://127.0.0.1:3000/user/bot/remove/",
                type:"post",
                data:{
                    bot_id:bot.id,
                },
                // 需要登录之后才能看的都要加
                headers:{
                    Authorization:"Bearer "+store.state.user.token,
                },
                success(resp){
                    if(resp.error_message==="success"){
                        refresh_bots();
                    }
                }
            })
        }

        return {
            bots,
            botadd,
            add_bot,
            remove_bot,
            update_bot,
        }
    }
}
</script>

<style scoped>
div.error-message{
    color:red
}
</style>