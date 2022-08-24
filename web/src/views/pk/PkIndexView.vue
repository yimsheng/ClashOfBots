<template>
<PlayGound v-if="$store.state.pk.status==='playing'"/>
<MatchGround v-if="$store.state.pk.status==='matching'"/>
<ResultBoard v-if="$store.state.pk.loser!='none'"/>
</template>

<script>

import PlayGound from '../../components/PlayGound.vue'
import MatchGround from '../../components/MatchGround.vue'
import ResultBoard from '../../components/ResultBoard.vue'
import {onMounted,onUnmounted} from 'vue'
import {useStore} from 'vuex'
export default{
    components:{
        PlayGound,
        MatchGround,
        ResultBoard,
    },
    setup(){
        
        const store=useStore();
        const socketUrl = `ws://127.0.0.1:3000/websocket/${store.state.user.token}/`;
        // 防止前一局的结果带入
        store.commit("updateLoser","none");

        let socket=null;
        onMounted(()=>{
            store.commit("updateOpponent",{
                username:"My rival",
                photo:"https://cdn.acwing.com/media/article/image/2022/08/09/1_1db2488f17-anonymous.png",
            })

            socket=new WebSocket(socketUrl);
            socket.onopen=()=>{
                console.log("connected!");
                store.commit("updateSocket",socket);
            }
            socket.onmessage=msg=>{
                const data=JSON.parse(msg.data);
                // 后端发来贺电说匹配好了
                if(data.event==="start-matching"){
                    store.commit("updateOpponent",{
                        username:data.opponent_username,
                        photo:data.opponent_photo,
                    });
                    setTimeout(()=>{
                        store.commit("updateStatus","playing");
                    },1000);
                    store.commit("updateGamemap",data.game);
                }else if(data.event==="move"){
                    //后端发来两个玩家都输入下一步了
                    const game=store.state.pk.gameObject;
                    const [snake0,snake1] = game.snakes;
                    snake0.set_direction(data.a_direction);
                    snake1.set_direction(data.b_direction);
                }else if(data.event==="result"){
                    //后端发来游戏结束
                    const game=store.state.pk.gameObject;
                    const [snake0,snake1] = game.snakes;
                    if(data.loser==="all"||data.loser==="A"){
                        snake0.status="dead";
                    }
                    if(data.loser==="all"||data.loser==="B"){
                        snake1.status="dead";
                    }
                    store.commit("updateLoser",data.loser);

                }
            }
            socket.onclose=()=>{
                console.log("disconnected");
            }
        });
        onUnmounted(()=>{
            socket.close();
            store.commit("updateStatus","matching");
        })



    }
}
</script>

<style scoped>

</style>