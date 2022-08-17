<template>
    <div v-if="!$store.state.user.pulling_info">
        <div class="row justify-content-md-center">
            
            <div class="col-3">
                <section>
                <form @submit.prevent="login">
                    
                        <label for="username" class="form-label"></label>
                        <input v-model="username" type="text" class="form-control" id="username" placeholder="username">
                 
                        <label for="password" class="form-label"></label>
                        <input v-model="password" type="password" class="form-control" id="password" placeholder="password">
                    
                    <div class="error-message">{{ error_message }}</div>
                    <button type="submit" >Log in</button>
                </form>
                </section>
            </div>
        </div>
    </div>
</template>

<script>
import { useStore } from 'vuex';
import { ref } from 'vue'
import router from '../../../router/index'

export default{
    components:{
        
    },
    setup(){
        const store = useStore();
        let username = ref('');
        let password = ref('');
        let error_message = ref('');

        
        const jwt_token=localStorage.getItem("jwt_token");
        
        if(jwt_token){
            
            store.commit("updateToken",jwt_token);
            store.dispatch("getInfo",{
                success(){
                    router.push({name: "home"});
                    store.commit("updatePullingInfo",false);
                },
                error(){
                    store.commit("updatePullingInfo",false);
                }
            })
        }else{
            store.commit("updatePullingInfo",false);
        }
        
        const login = () =>{
            //清空
            error_message.value="";
            store.dispatch("login",{
                username: username.value,
                password: password.value,
                success() {
                    store.dispatch("getInfo", {
                        success() {
                            router.push({ name: 'home' });
                            // console.log(store.state.user);
                        }
                    })
                },
                error() {
                    error_message.value = "Wrong Username and password";
                }
            })
            
        }
        return{
            username,
            password,
            error_message,
            login,
        }
    }
}
</script>

<style scoped>
button {
    width: 100%;
}
div.error-message {
    color: red;
}

section {
margin-top: 50px;
position: relative;
width: 110%;
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
width: 400px;
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