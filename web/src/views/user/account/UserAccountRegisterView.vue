<template>
    <div>
        <div class="row justify-content-md-center">
            <div class="col-3">
                <section>
                <form @submit.prevent="register">
                    <div class="mb-3">
                        <label for="username" class="form-label"></label>
                        <input v-model="username" type="text" class="form-control" id="username" placeholder="Username">
                    </div>
                    <div class="mb-3">
                        <label for="password" class="form-label"></label>
                        <input v-model="password" type="password" class="form-control" id="password" placeholder="Password">
                    </div>
                    <div class="mb-3">
                        <label for="password" class="form-label"></label>
                        <input v-model="confirmedPassword" type="password" class="form-control" id="confirmedPassword" placeholder="Confirmed Password">
                    </div>
                    <div class="error-message">{{ error_message }}</div>
                    <button type="submit">Register</button>
                </form>
                </section>
            </div>
        </div>
    </div>
</template>

<script>
import { ref } from 'vue'
import router from '../../../router/index';

import $ from 'jquery';

export default{
    components:{
  
    },
    setup(){
        let username = ref('');
        let password = ref('');
        let confirmedPassword = ref('');
        let error_message = ref('');
        //触发函数
        const register = () =>{
            //不会修改state所以放在这里而不是user里
            $.ajax({
                url:"http://127.0.0.1:3000/user/account/register/",
                type:"post",
                data:{
                    username:username.value,
                    password:password.value,
                    confirmedPassword:confirmedPassword.value,
                },
                //"success":function()
                success(resp){
                    if(resp.error_message==="success"){
                        router.push({name:"user_account_login"});
                    }else{
                        error_message.value=resp.error_message;
                    }
                },
            })
           
        }
        return{
            username,
            password,
            confirmedPassword,
            error_message,
            register,
        }
    }
}
</script>

<style scoped>

div.error-message {
    color: red;
}
section {
margin-top: 50px;
position: relative;
width: 100%;
height: 100%;
display: flex;
align-items: center;
justify-content: center;
/* background-image: linear-gradient(94.3deg, rgba(26, 33, 64, 1) 10.9%, rgba(81, 84, 115, 1) 87.1%); */
overflow: hidden;
        }




form {
position: relative;
z-index: 3;
width: 600px;
background-color: rgba(255, 255, 255, .1);
padding: 45px 30px;
border-radius: 8px;
box-shadow: 0 20px 50px rgba(0, 0, 0, .1);
border: 1px solid rgba(255, 255, 255, .2);
backdrop-filter: blur(20px);
        }

form p {
color: #fff;
display: block;
text-align: center;
margin: 0 0 30px 0;
        }

input {
width: 100%;
height: 50px;
border-radius: 8px;
background-color: transparent;
border: 1px solid rgba(255, 255, 255, .5);
margin-bottom: 15px;
padding-left: 15px;
color: #fff;
outline: none;
        }

input::placeholder {
color: #fff;
        }


button {
margin-top: 20px;
width: 100%;
height: 50px;
border: 0;
background-image: linear-gradient(to right, #02AAB0 0%, #00CDAC 51%, #02AAB0 100%);
background-size: 200% auto;
color: #fff;
border-radius: 8px;
outline: none;
cursor: pointer;
box-shadow: 0 20px 40px rgba(0, 0, 0, .1);
transition: all ease .4s;
        }

button:hover {
background-position: right center;
color: #fff;
text-decoration: none;
        }
</style>