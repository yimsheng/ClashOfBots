import { AcGameObject } from "./AcGameObject";
import { Cell } from "./Cell";

export class Snake extends AcGameObject{
    constructor(info,gamemap){
        super();
        this.id=info.id;
        this.color=info.color;
        this.gamemap=gamemap;

        this.cells=[new Cell(info.r,info.c)];//存放蛇身体，cells[0]是蛇头
        this.next_cell=null;//下一步的目标位置
        this.speed=5; //每秒走五个格子
        //-1：没有指令，0,1,2,3:上右下左
        this.direction=-1;
        //idle:静止，move：正在移动，dead：表示寄了
        this.status="idle";
        this.dr = [-1, 0, 1, 0];  // 4个方向行的偏移量
        this.dc = [0, 1, 0, -1];  // 4个方向列的偏移量

        this.step=0;//表示回合数
        this.eps=1e-2;

        //处理蛇的眼睛
        this.eye_direction = 0;
        if (this.id === 1) this.eye_direction = 2;  // 左下角的蛇初始朝上，右上角的蛇朝下

        this.eye_dx = [  // 蛇眼睛不同方向的x的偏移量
            [-1, 1],
            [1, 1],
            [1, -1],
            [-1, -1],
        ];
        this.eye_dy = [  // 蛇眼睛不同方向的y的偏移量
            [-1, -1],
            [-1, 1],
            [1, 1],
            [1, -1],
        ]
    }

    
    start(){

    }

    set_direction(d){
        this.direction=d;
    }
    //检测当前回合蛇的长度是否会增加
    check_tail_increasing(){
        if (this.step <= 5) return true;
        if (this.step % 3 === 1) return true;
        return false;
    }


    next_step(){
        //将蛇的状态变为走下一步
        const d = this.direction;
        this.next_cell=new Cell(this.cells[0].r+this.dr[d], this.cells[0].c + this.dc[d]);
        this.eye_direction=d;
        this.direction=-1;//清空操作
        this.status='move';
        this.step++;

        const k=this.cells.length;
        for(let i=k;i>0;i--){
            this.cells[i] = JSON.parse(JSON.stringify(this.cells[i - 1]));
        }
        // if(!this.gamemap.check_valid(this.next_cell)){
        //     this.status='dead';
        // }移到后端来判断

    }

    update_move(){
        
         const dx=this.next_cell.x-this.cells[0].x;
         const dy = this.next_cell.y - this.cells[0].y;
         const distance = Math.sqrt(dx * dx + dy * dy);

        if(distance<this.eps){
            //讲目标点作为真实的新头，再把目标点清空
            this.cells[0]=this.next_cell;
            this.next_cell=null;
            this.status="idle";//走完了，停下

            if(!this.check_tail_increasing()){
                this.cells.pop();
            }

        }else{
            //每秒移动五格：每帧里将蛇头横坐标加上timedelta（两帧之间的间隔）*speed
            const move_distance = this.speed*this.timedelta/1000;
            this.cells[0].x+=move_distance*dx/distance;
            this.cells[0].y+=move_distance*dy/distance;

            if(!this.check_tail_increasing()){
                const k = this.cells.length;
                //tail是最后一个，tail_target是倒数第二个
                const tail = this.cells[k-1],tail_target=this.cells[k-2];
                //把tail移到tail target
                const tail_dx = tail_target.x-tail.x;
                const tail_dy = tail_target.y-tail.y;
                tail.x+=move_distance*tail_dx/distance;
                tail.y+=move_distance*tail_dy/distance;
            }

        }
    }
    update(){
        if(this.status==='move'){
            this.update_move();
        }
        
        this.render();
    }

    render(){
        //单格的长度
        const L = this.gamemap.L;
        const ctx = this.gamemap.ctx;

        ctx.fillStyle=this.color;

        if (this.status === "dead") {
            ctx.fillStyle = "gray";
        }

        for(const cell of this.cells){
            ctx.beginPath();
            ctx.arc(cell.x*L,cell.y*L,L/2*0.8,0,Math.PI*2);
            ctx.fill();
        }
        //在两个球中间画矩形填充
        for (let i = 1; i < this.cells.length; i ++ ) {
            const a = this.cells[i - 1], b = this.cells[i];
            //两个球重合了不用画了
            if (Math.abs(a.x - b.x) < this.eps && Math.abs(a.y - b.y) < this.eps)
                continue;
            //竖方向 x一样
            if (Math.abs(a.x - b.x) < this.eps) {
                ctx.fillRect((a.x - 0.4) * L, Math.min(a.y, b.y) * L, L * 0.8, Math.abs(a.y - b.y) * L);
            //横方向 y一样
            } else {
                ctx.fillRect(Math.min(a.x, b.x) * L, (a.y - 0.4) * L, Math.abs(a.x - b.x) * L, L * 0.8);
            }
        }
        //画蛇的眼睛
        ctx.fillStyle = "black";
        for (let i = 0; i < 2; i ++ ) {
            const eye_x = (this.cells[0].x + this.eye_dx[this.eye_direction][i] * 0.15) * L;
            const eye_y = (this.cells[0].y + this.eye_dy[this.eye_direction][i] * 0.15) * L;

            ctx.beginPath();
            ctx.arc(eye_x, eye_y, L * 0.05, 0, Math.PI * 2);
            ctx.fill();
        }

    }
}