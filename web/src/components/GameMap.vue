<template>
    <div ref="parent" class="gamemap">
        //tabindex is for user input
        <canvas ref="canvas" tabindex="0"></canvas>
    </div>
</template>

<script>
import {GameMap} from "@/assets/scripts/GameMap";
import {ref,onMounted} from "vue";
export default{
    setup(){
        let parent = ref(null);
        let canvas = ref(null);
        //组件挂载完要执行哪些操作
        onMounted(()=>{
            //创建GameMap对象
            new GameMap(canvas.value.getContext('2d'),parent.value)
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