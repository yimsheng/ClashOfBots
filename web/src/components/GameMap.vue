<template>



 
 <div class="col-2" v-if="$store.state.pk.my_snake==='B'" style="background-color:#f2c162;font-size:30px;text-align:center;border-radius: 20px;">YOU</div>
    <div ref="parent" class="gamemap">
        //tabindex is for user input
        <canvas ref="canvas" tabindex="0"></canvas>
    </div>
<div class="col-2" v-if="$store.state.pk.my_snake==='A'" style="background-color:#FF869E;font-size:30px;text-align:center;border-radius: 20px;">YOU</div>
</template>

<script>
import {GameMap} from "@/assets/scripts/GameMap";
import {ref,onMounted } from "vue";
import {useStore} from 'vuex';
export default{
    setup(){
        const store = useStore();
        let parent = ref(null);
        let canvas = ref(null);
        
        //组件挂载完要执行哪些操作
        onMounted(()=>{
            //创建GameMap对象
            store.commit("updateGameObject",new GameMap(canvas.value.getContext('2d'),parent.value,store));
            
            });

        return {
            parent,
            canvas
            }
    }
}

</script>

<style scoped>
div.gamemap{
    width:100%;
    height:100%;
    /* 居中 */
    display: flex;
    justify-content: center;
    align-items: center;
    }
</style>