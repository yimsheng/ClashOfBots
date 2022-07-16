const AC_GAME_OBJECT =[];

export class AcGameObject{
    constructor(){
        AC_GAME_OBJECT.push(this);
        this.timedelta=0;
        this.has_called_start=false;
    }
    //第一帧执行start
    start(){
    }
    //后面每一帧执行update
    update(){

    }
    on_destroy(){

    }
    destroy(){
        this.on_destroy();
        for (let i in AC_GAME_OBJECT){
            const obj=AC_GAME_OBJECT[i];
            if(obj==this){
                AC_GAME_OBJECT.splice(i);
                break;
            }
        }
    }
}
let last_timestamp;//上一次执行的时刻
const step=timestamp=>{
    for(let obj of AC_GAME_OBJECT){
        if(!obj.has_called_start){
            obj.has_called_start=true;
            obj.start();
        }else{
            obj.timedelta=timestamp-last_timestamp;
            obj.update();
        }
    }
    last_timestamp=timestamp;
    requestAnimationFrame(step)//每一帧都执行这个函数
}

requestAnimationFrame(step)