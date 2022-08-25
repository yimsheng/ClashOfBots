import { AcGameObject } from "./AcGameObject";
import { Snake } from "./Snake";
import { Wall}  from './Wall';

export class GameMap extends AcGameObject{
    constructor(ctx,parent,store){
        super();
        this.ctx=ctx;
        this.parent=parent;
        this.store=store;
        this.L=0;

        this.rows=13;
        this.cols=14;
        this.inner_walls_count=25;
        this.walls=[];
        
        this.snakes=[
            new Snake({id: 0, color: "#FF869E", r: this.rows - 2, c: 1}, this),
            new Snake({id: 1, color: "#f2c162", r: 1, c: this.cols - 2}, this),
        ];
    }

    // check_connectivity(g,sx,sy,tx,ty){
    //     if(sx==tx&&sy==ty) return true;
    //     g[sx][sy]=true;

    //     let dx=[-1,0,1,0], dy=[0,1,0,-1];
    //     for(let i=0;i<4;i++){
    //         let x=sx+dx[i],y=sy+dy[i];
    //         if(!g[x][y]&&this.check_connectivity(g,x,y,tx,ty)) return true;
    //     }
    //     return false;

    // }

    create_walls(){
        const g=this.store.state.pk.gamemap;
        for(let r=0;r<this.rows;r++){
            for(let c=0;c<this.cols;c++){
                if(g[r][c]){
                    this.walls.push(new Wall(r,c,this));
                }
            }
        }
        // return true;
    }

    add_listening_events(){
        if(this.store.state.record.is_record){
            let k=0;
            const a_steps = this.store.state.record.a_steps;
            const b_steps = this.store.state.record.b_steps;
            const [snake0,snake1] = this.snakes;
            const loser = this.store.state.record.record_loser;
            const interval_id = setInterval(()=>{
                if(k>=a_steps.length-1){
                    if (loser === "all" || loser === "A") {
                        snake0.status = "dead";
                    }
                    if (loser === "all" || loser === "B") {
                        snake1.status = "dead";
                    }

                    clearInterval(interval_id);

                }else{
                    snake0.set_direction(parseInt(a_steps[k]));
                    snake1.set_direction(parseInt(b_steps[k]));
                }
                k++;
            },300);
        }else{
            this.ctx.canvas.focus();
            this.ctx.canvas.addEventListener("keydown",e => {
                let d=-1;
                if(e.key==='w') d=0;
                else if (e.key === 'd') d=1;
                else if (e.key === 's') d=2;
                else if (e.key === 'a') d=3;
                if(d>=0){
                    this.store.state.pk.socket.send(JSON.stringify({
                        event:"move",
                        direction:d,
                    }))
                }

            });
        }
    }

    start(){

        this.create_walls();
        this.add_listening_events();
    }

    update_size(){
        this.L = parseInt(Math.min(this.parent.clientWidth / this.cols, this.parent.clientHeight / this.rows));
        this.ctx.canvas.width=this.L*this.cols;
        this.ctx.canvas.height=this.L*this.rows;
    }

    check_ready(){ //判断两条蛇是否都准备好下一轮了
        for(const snake of this.snakes){
            if(snake.status!=='idle') return false;
            if(snake.direction===-1) return false;
        }
        return true;
    }
    next_step(){    //让两条蛇进入下一回合
        for (const snake of this.snakes) {
            snake.next_step();
        }
    }

    check_valid(cell){
        //没有撞到两条蛇的身体和墙
        for(const wall of this.walls){
            if(wall.r===cell.r &&wall.c===cell.c){
                return false;
            }
        }
        for(const snake of this.snakes){
            let k = snake.cells.length;
            if(!snake.check_tail_increasing()){//当蛇尾会前进的时候蛇尾不用判断
                k--;
            }

            for(let i=0;i<k;i++){
                if(snake.cells[i].r===cell.r &&snake.cells[i].c===cell.c)
                return false;
            }
        }
        return true;
    }

    update(){
        this.update_size();
        //如果两条蛇都准备好了，就进入下一回合
        if(this.check_ready()){
            this.next_step();
        }

        this.render();
    }
    render(){
        const color_even="#B2C8DF", color_odd="#C4D7E0";
        for(let r=0;r<this.rows;r++){
            for(let c=0;c<this.cols;c++){
                if ((r+c)%2==0){
                    this.ctx.fillStyle=color_even;
                }else{
                    this.ctx.fillStyle=color_odd;
                }
                this.ctx.fillRect(c*this.L,r*this.L,this.L,this.L)
            }
        }

    }
}