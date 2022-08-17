<template>
    <div class="matchground">
        <div class="row">
            <div class="col-4">
                <div class="user-photo">    
                    <img :src="$store.state.user.photo" alt="">
                </div>
                <div class="user-username">    
                    {{$store.state.user.username}}
                </div>
                
            </div>

            <div class="col-4">
                <div class="user-select-bot">
                    <select v-model="select_bot" class="form-select" aria-label="Default select example">
                    <option value="-1" selected>You</option>
                    <option v-for="bot in bots" :key="bot.id" :value="bot.id">
                        {{bot.title}}
                    </option> 
                    </select>   
                </div>
            </div>
            <div class="col-4">
                <div class="user-photo">    
                    <img :src="$store.state.pk.opponent_photo" alt="">
                </div>
                <div class="user-username">    
                    {{$store.state.pk.opponent_username}}
                </div>
            </div>
            <div class="col-12" style="text-align: center; padding-top: 10vh;">
                <button @click="click_match_btn" type="button" class="btn btn-two">
                   {{ match_btn_info }}
                </button>
                
            </div>

        </div>
    </div>

</template>

<script>
import { ref } from 'vue'
import { useStore } from 'vuex';
import $ from 'jquery';


export default{
   setup(){
       const store=useStore();
       let match_btn_info=ref("Start Matching");
       let bots = ref([]);
       let select_bot=ref("-1");

       const click_match_btn=()=>{
           if(match_btn_info.value==="Start Matching"){
               match_btn_info.value="Stop";
               store.state.pk.socket.send(JSON.stringify({
                   event:"start-matching",
                   bot_id:select_bot.value,
               }));
           }else{
               match_btn_info.value="Start Matching";
               store.state.pk.socket.send(JSON.stringify({
                   event:"stop-matching",
               }));
           }
       }

       const refresh_bots=()=>{
           $.ajax({
               url:"http://127.0.0.1:3000/user/bot/getlist/",
               type:"get",
               headers:{
                   Authorization:"Bearer "+store.state.user.token,
               },
               success(resp){
                   bots.value=resp;
               }
           })
       }
       refresh_bots();
       return{
           match_btn_info,
           click_match_btn,
           bots,
           select_bot,
       }
   }
}

</script>

<style scoped>
div.matchground{
    width:60vw;
    height: 70vh;
    margin:40px auto;
    background-image: linear-gradient(230deg,rgb(70, 133, 206),rgba(55, 204, 92, 0.603));
    border-radius: 20px;
    }
div.user-photo{
    text-align: center;
    padding-top: 10vh;
}
div.user-photo > img {
    border-radius: 50%;
    width:20vh;
}
div.user-username{
    text-align: center;
    font-size:24px;
    font-weight:600;
    color:white;
    padding-top:4vh;
}
div.user-select-bot{
    padding-top:20vh;
}
div.user-select-bot>select{
    width:60%;
    margin:0 auto;
}
.btn {
  line-height: 50px;
  height: 60px;
  text-align: center;
  width: 250px;
  cursor: pointer;
  font-size: 25px;
}

.btn-two {
  color: rgb(217, 245, 243);
  transition: all 0.5s;
  position: relative; 
  
}
.btn-two span {
  z-index: 2; 
  display: block;
  position: absolute;
  width: 100%;
  height: 100%; 
}
.btn-two::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  z-index: 1;
  transition: all 0.5s;
  border: 1px solid rgba(255,255,255,0.2);
  background-color: rgba(255,255,255,0.1);
}
.btn-two::after {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  z-index: 1;
  transition: all 0.5s;
  border: 1px solid rgba(255,255,255,0.2);
  background-color: rgba(255,255,255,0.1);
}
.btn-two:hover::before {
  transform: rotate(-45deg);
  background-color: rgba(255,255,255,0);
}
.btn-two:hover::after {
  transform: rotate(45deg);
  background-color: rgba(255,255,255,0);
}

</style>