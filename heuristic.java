import ilog.concert.IloException;
import ilog.concert.IloNumVar;
import ilog.cplex.IloCplex;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.PriorityQueue;

public class heuristic {

    public static boolean checkConstraints(double[] y,double[][] x,double[] s,double[] d){
        int n=y.length;
        int m=x.length;
        for(int j=0;j<n;j++){
            if(y[j]!=0&&y[j]!=1)
                return false;
        }
        for(int i=0;i<m;i++){
            double xsum=0;
            for(int j=0;j<n;j++){
                if(x[i][j]<0)
                    return false;
                xsum+=x[i][j];
            }
            if(xsum!=1)
                return false;
        }
        for(int j=0;j<n;j++){
            double dsum=0;
            for(int i=0;i<m;i++){
                dsum+=d[i]*x[i][j];
            }
            if(dsum>s[j]*y[j])
                return false;
        }
        return true;
    }

    public static int[] getStringValues(String s){
        String[] ss=s.split(" ");
        ArrayList<Integer> values=new ArrayList<Integer>();
        for(int i=0;i<ss.length;i++){
            if(!ss[i].equals(""))
                if(ss[i].contains("."))
                    values.add(Integer.valueOf(ss[i].substring(0,ss[i].length()-1)));
                else
                    values.add(Integer.valueOf(ss[i]));
        }
        int[] ans=new int[values.size()];
        for(int i=0;i<values.size();i++){
            ans[i]=values.get(i);
        }
        return ans;
    }

    public static double getFunctionValue(double[] y,double[][] x,double[] f,double[][] c){
        double funValue=0;
        int n=y.length;
        int m=x.length;
        for(int j=0;j<n;j++){
            funValue+=f[j]*y[j];
        }
        for(int i=0;i<m;i++){
            for(int j=0;j<n;j++){
                funValue+=c[i][j]*x[i][j];
            }
        }
        return funValue;
    }

    public static void main(String[] args) {
        BufferedWriter wr=null;
        try{
            wr=new BufferedWriter(new FileWriter(new File("heuristic.txt")));
        } catch (IOException e) {
            e.printStackTrace();
        }
        for(int t=1;t<=71;t++){
            int n=0;
            int m=0;
            double[] s=null;  //sj
            double[] d=null;  //di
            double[] f=null;  //fj
            double[][] c=null;  //cij
            double maxcost=0;
            try {
                BufferedReader re=new BufferedReader(new FileReader(new File("instances/p"+t)));
                int[] v=getStringValues(re.readLine());
                n=v[0];
                m=v[1];
                s=new double[n];
                d=new double[m];
                f=new double[n];
                c=new double[m][n];
                for(int j=0;j<n;j++){
                    v=getStringValues(re.readLine());
                    f[j]=v[0];
                    s[j]=v[1];
                }
                for(int k=0;k<m/10;k++){
                    v=getStringValues(re.readLine());
                    for(int i=0;i<10;i++){
                        d[k*10+i]=v[i];
                    }
                }
                for(int i=0;i<m;i++){
                    for(int j=0;j<n/10;j++){
                        v=getStringValues(re.readLine());
                        for(int k=0;k<10;k++){
                            c[i][j*10+k]=v[k];
                            if(v[k]>maxcost)
                                maxcost=v[k];
                        }


                    }
                }

            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            double[] superCost=new double[m];
            for(int i=0;i<m;i++){
                superCost[i]=maxcost;
            }
            double[] y=new double[n];
            long start=System.currentTimeMillis();
            double ans1=add(s,d,f,c,superCost);
            long time1=(System.currentTimeMillis()-start)/1000;
            start=System.currentTimeMillis();
            double ans2=drop(s,d,f,c);
            long time2=(System.currentTimeMillis()-start)/1000;
            start=System.currentTimeMillis();
            double ans3=addWithLowerBound(s,d,f,c,superCost);
            long time3=(System.currentTimeMillis()-start)/1000;
            for(int j=0;j<n;j++){
                y[j]=1;
            }
            start=System.currentTimeMillis();
            double ans4=interchangeDROP(y,s,d,f,c);
            long time4=(System.currentTimeMillis()-start)/1000;
            try {
                wr.write(t+"");
                wr.newLine();
                wr.write("add:"+ans1+"/"+time1+"s");
                wr.newLine();
                wr.write("drop:"+ans2+"/"+time2+"s");
                wr.newLine();
                wr.write("addLowerBound:"+ans3+"/"+time3+"s");
                wr.newLine();
                wr.write("interchangeDrop:"+ans4+"/"+time4+"s");
                wr.newLine();
                wr.flush();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static double getFlowValue(double[][] x,double[][] c){
        double flowValue=0;
        int n=x[0].length;
        int m=x.length;
        for(int i=0;i<m;i++){
            for(int j=0;j<n;j++){
                flowValue+=c[i][j]*x[i][j];
            }
        }
        return flowValue;
    }

    public static double[][] optimizeX(double[] y,double[] s,double[] d,double[][] c){
        int n=y.length;
        int m=d.length;
        double[][]ans=new double[m][n];
        try {
            IloCplex model=new IloCplex();
            double[] lb=new double[m*n];
            double[] ub=new double[m*n];
            for(int i=0;i<m;i++){
                for(int j=0;j<n;j++){
                    ub[i*n+j]=y[j];
                }
            }
            IloNumVar[] x=model.numVarArray(d.length*y.length,lb,ub);

            double[] o=new double[m*n];
            for(int i=0;i<m;i++){
                for(int j=0;j<n;j++){
                    o[i*n+j]=c[i][j];
                }
            }
            model.addMinimize(model.scalProd(x,o));

            double[] c1=new double[n];
            for(int j=0;j<n;j++){
                c1[j]=1;
            }
            for(int i=0;i<m;i++){
                IloNumVar[] xx=new IloNumVar[n];
                for(int j=0;j<n;j++){
                    xx[j]=x[i*n+j];
                }
                model.addEq(model.scalProd(xx,c1),1);
            }

            double[] c2=d;
            for(int j=0;j<n;j++){
                IloNumVar[] xx=new IloNumVar[m];
                for(int i=0;i<m;i++){
                    xx[i]=x[i*n+j];
                }
                model.addLe(model.scalProd(xx,c2),s[j]*y[j]);
            }

            if(model.solve()){
                double[] xx=model.getValues(x);
                for(int i=0;i<m;i++){
                    for(int j=0;j<n;j++){
                        ans[i][j]=xx[i*n+j];
                    }
                }
            }
            model.close();
        } catch (IloException e) {
            e.printStackTrace();
        }
        return ans;
    }

    public static double add(double[] olds,double[] d,double[] oldf,double[][] oldc,double[] superCost){
        int n=olds.length+1;
        int m=d.length;

        double[] y=new double[n-1];
        for(int j=0;j<n-1;j++){
            y[j]=1;
        }
        if(optimizeX(y,olds,d,oldc)==null){
            System.out.println("It's infeasible.");
            return -1;
        }

        //加一个super plant, 其有大容量和高cost ,放在 f[0],s[0],f[0], c[][0],设为开设
        // superCost 长度为m 代表super plant 的 cij
        y=new double[n];
        double[] s=new double[n];
        for(int i=0;i<m;i++){
            s[0]+=d[i];
        }
        for(int j=1;j<n;j++){
            s[j]=olds[j-1];
        }
        double[] f=new double[n];
        f[0]=30000;//用不到,没有影响
        for(int j=1;j<n;j++){
            f[j]=oldf[j-1];
        }
        double[][] c=new double[m][n];
        for(int i=0;i<m;i++){
            c[i][0]=superCost[i];
            for(int j=1;j<n;j++){
                c[i][j]=oldc[i][j-1];
            }
        }

        //开设super plant 作为起始情况，开始迭代
        y[0]=1;
        boolean[] check=new boolean[n];
        double[][] xlast=optimizeX(y,s,d,c);
        double vlast=getFlowValue(xlast,c);
        while(true){
            double vmax=-Double.MAX_VALUE;
            int yindex=-1;
            for(int j=0;j<n;j++){
                if(y[j]==1||check[j])
                    continue;
                y[j]=1;
                double[][] x=optimizeX(y,s,d,c);
                double v=vlast-getFlowValue(x,c)-f[j];
                if(v<=0)
                    check[j]=true;
                if ( v > vmax) {
                    vmax = v;
                    yindex=j;
                    xlast=x;
                }
                y[j]=0;
            }
            if (vmax<=0)
                break;
            y[yindex]=1;
            vlast=vlast-f[yindex]-vmax;
        }
        boolean open=false;
        for(int i=0;i<m;i++){
            if(xlast[i][0]==1)
                open=true;
        }
        if(!open)
            y[0]=0;
        System.out.println("cost:"+getFunctionValue(y,xlast,f,c));
        //注意结果是不包含y[0] ：super plant 的，所以输出的话注意再转换一下
        if(y[0]==1){
            System.out.println("The super plant is opened, check the superCost.");
            return -1;
        }
        System.out.println("y:");
        for(int j=1;j<n;j++){
            System.out.print(y[j]+" ");
        }
        System.out.println("x:");
        for(int i=0;i<m;i++){
            for(int j=1;j<n;j++){
                System.out.print(xlast[i][j]+" ");
            }
            System.out.println("");
        }
        return getFunctionValue(y,xlast,f,c);
    }

    public static double drop(double[] s,double[] d,double[] f,double[][] c) {
        int n=s.length;
        int m=d.length;
        double[] y=new double[n];
        boolean[] check=new boolean[n];
        for(int j=0;j<n;j++){
            y[j]=1;
        }

        double[][] xlast=optimizeX(y,s,d,c);
        if(xlast==null){
            System.out.println("It's infeasible.");
            return -1;
        }
        double vlast=getFlowValue(xlast,c);

        while(true){
            double vmax=-Double.MAX_VALUE;
            int yindex=-1;
            for(int j=0;j<n;j++){
                if(y[j]==0||check[j])//!!!y[j]==0不要写错，注意add中的  //可能越往后，省钱越少，像add那样，但是有点复杂，没加
                    continue;
                y[j]=0;
                double[][] x=optimizeX(y,s,d,c);
                if(x==null)
                    check[j]=true;
                else {
                    double v=f[j] + vlast -getFlowValue(x,c);
                    if ( v > vmax) {
                        vmax = v;
                        yindex=j;
                        xlast=x;
                    }
                }
                y[j]=1;
            }
            if (vmax<=0)
                break;
            y[yindex]=0;
            vlast=vlast+f[yindex]-vmax;
        }
        System.out.println("cost:"+getFunctionValue(y,xlast,f,c));
        System.out.println("y:");
        for(int j=0;j<n;j++){
            System.out.print(y[j]+" ");
        }
        System.out.println("x:");
        for(int i=0;i<m;i++){
            for(int j=0;j<n;j++){
                System.out.print(xlast[i][j]+" ");
            }
            System.out.println("");
        }
        return getFunctionValue(y,xlast,f,c);
    }

    public static double getLowerBound(double[] k,double s,double[] d){
        double ans=Double.MAX_VALUE;
        int m=d.length;
        try {
            IloCplex model=new IloCplex();
            double[] lb=new double[m];
            double[] ub=new double[m];
            for(int i=0;i<m;i++){
                lb[i]=0;
                ub[i]=1;
            }
            IloNumVar[] x=model.numVarArray(m,lb,ub);
            model.addMaximize(model.scalProd(x,k));
            model.addLe(model.scalProd(x,d),s);
            if(model.solve()){
                ans=model.getObjValue();
            }
            model.close();
        } catch (IloException e) {
            e.printStackTrace();
        }
        return ans;
    }

    public static double addWithLowerBound(double[] olds,double[] d,double[] oldf,double[][] oldc,double[] superCost){
        int n=olds.length+1;
        int m=d.length;

        double[] y=new double[n-1];
        for(int j=0;j<n-1;j++){
            y[j]=1;
        }
        if(optimizeX(y,olds,d,oldc)==null){
            System.out.println("It's infeasible.");
            return -1;
        }

        //加一个super plant, 其有大容量和高cost ,放在 f[0],s[0],f[0], c[][0],设为开设
        // superCost 长度为m 代表super plant 的 cij
        y=new double[n];
        double[] s=new double[n];
        for(int i=0;i<m;i++){
            s[0]+=d[i];
        }
        for(int j=1;j<n;j++){
            s[j]=olds[j-1];
        }
        double[] f=new double[n];
        f[0]=0;//用不到,没有影响
        for(int j=1;j<n;j++){
            f[j]=oldf[j-1];
        }
        double[][] c=new double[m][n];
        for(int i=0;i<m;i++){
            c[i][0]=superCost[i];
            for(int j=1;j<n;j++){
                c[i][j]=oldc[i][j-1];
            }
        }

        //按照cost给每个demand排序
        Comparator<node> ct1=new Comparator<node>() {
            @Override
            public int compare(node o1, node o2) {
                return o1.value<o2.value?-1:0;
            }
        };
        node[][] nodes=new node[m][n];
        for(int i=0;i<m;i++){
            for(int j=0;j<n;j++){
                nodes[i][j]=new node(j,c[i][j]);
            }
            Arrays.sort(nodes[i],ct1);
        }

        //开设super plant 作为起始情况，开始迭代
        y[0]=1;
        boolean[] check=new boolean[n];
        double[][] xlast=optimizeX(y,s,d,c);
        double vlast=getFlowValue(xlast,c);
        Comparator<node> ct2=new Comparator<node>() {
            @Override
            public int compare(node o1, node o2) {
                return o1.value>o2.value?-1:0;
            }
        };
        while(true){

            PriorityQueue<node> lowerBound=new PriorityQueue<node>(ct2);
            for(int j=0;j<n;j++){
                if(y[j]==1||check[j])
                    continue;
                double[] k=new double[m];
                for(int i=0;i<m;i++){
                    int count=0;
                    while(count<n){
                        int index=nodes[i][count].index;
                        if(check[index]){
                            count++;
                        }else {
                            if(index==j){
                                while(true){
                                    count++;
                                    if(count==n){
                                        k[i]=c[i][j];
                                    }else {
                                        if(check[nodes[i][count].index])
                                            continue;
                                        else
                                            k[i]=c[i][nodes[i][count].index]-c[i][j];
                                    }
                                    break;
                                }
                            }else {
                                k[i]=0;
                            }
                            break;
                        }
                    }
                }
                //use tool to solve the LBS
                double lbs=getLowerBound(k,s[j],d);
                lowerBound.add(new node(j,lbs));
            }

            double vmax=-Double.MAX_VALUE;
            int yindex=-1;
            while(!lowerBound.isEmpty()){
                node t=lowerBound.poll();
                if(t.value<vmax)
                    break;

                int j=t.index;
                y[j]=1;
                double[][] x=optimizeX(y,s,d,c);
                double v=vlast-getFlowValue(x,c)-f[j];
                if(v<=0)
                    check[j]=true;
                if ( v > vmax) {
                    vmax = v;
                    yindex=j;
                    xlast=x;
                }
                y[j]=0;
            }
            if (vmax<=0)
                break;
            y[yindex]=1;
            vlast=vlast-f[yindex]-vmax;
        }
        boolean open=false;
        for(int i=0;i<m;i++){
            if(xlast[i][0]==1)
                open=true;
        }
        if(!open)
            y[0]=0;
        System.out.println("cost:"+getFunctionValue(y,xlast,f,c));
        //注意结果是不包含y[0] ：super plant 的，所以输出的话注意再转换一下
        if(y[0]==1){
            System.out.println("The super plant is opened, check the superCost.");
            return -1;
        }
        System.out.println("y:");
        for(int j=1;j<n;j++){
            System.out.print(y[j]+" ");
        }
        System.out.println("x:");
        for(int i=0;i<m;i++){
            for(int j=1;j<n;j++){
                System.out.print(xlast[i][j]+" ");
            }
            System.out.println("");
        }
        return getFunctionValue(y,xlast,f,c);
    }

    public static void interchangeADD(double[] y,double[] s,double[] d,double[] f,double[][] c){
        double[][]xlast=optimizeX(y,s,d,c);
        if(xlast==null){
            System.out.println("It's an infeasible solution, please input again.");
            return;
        }
        int n=y.length;
        int m=d.length;
        double vlast=getFlowValue(xlast,c);

        while(true){
            int count=0;
            while(count<n){
                if(y[count]==1){
                    y[count]=0;
                    double vmax=-Double.MAX_VALUE;
                    int yindex=-1;
                    for(int j=0;j<n;j++){
                        if(y[j]==1)
                            continue;
                        y[j]=1;
                        double[][] x=optimizeX(y,s,d,c);
                        if(x==null)
                            continue;
                        double v=vlast-getFlowValue(x,c)-f[j];
                        if ( v > vmax) {
                            vmax = v;
                            yindex=j;
                        }
                        y[j]=0;
                    }
                    if (vmax<=0){//可变少不可变多！！！！！！！！！！！！
                        break;
                    }else if(yindex!=count){
                        y[yindex]=1;
                        vlast=vlast-f[yindex]-vmax;
                        break;
                    }
                    y[count]=1;
                }
                count++;
            }
            if(count==n){
                break;
            }
        }
        double[][] x=optimizeX(y,s,d,c);
        System.out.println("cost:"+getFunctionValue(y,x,f,c));
        System.out.println("y:");
        for(int j=0;j<n;j++){
            System.out.print(y[j]+" ");
        }
        System.out.println("x:");
        for(int i=0;i<m;i++){
            for(int j=0;j<n;j++){
                System.out.print(x[i][j]+" ");
            }
            System.out.println("");
        }
    }

    public static double interchangeDROP(double[] y,double[] s,double[] d,double[] f,double[][] c){
        double[][]xlast=optimizeX(y,s,d,c);
        if(xlast==null){
            System.out.println("It's an infeasible solution, please input again.");
            return -1;
        }
        int n=y.length;
        int m=d.length;
        double vlast=getFlowValue(xlast,c);

        while(true){
            int count=0;
            while(count<n){
                if(y[count]==0){
                    y[count]=1;

                    double vmax=-Double.MAX_VALUE;
                    int yindex=-1;
                    for(int j=0;j<n;j++){
                        if(y[j]==0)
                            continue;
                        y[j]=0;
                        double[][] x=optimizeX(y,s,d,c);
                        if(x==null)
                            continue;
                        else {
                            double v=f[j] + vlast -getFlowValue(x,c);
                            if ( v > vmax) {
                                vmax = v;
                                yindex=j;
                            }
                        }
                        y[j]=1;
                    }
                    if (vmax<=0)//可变多不可变少！！！！！！！！！！！！
                        break;
                    if(yindex!=count){
                        y[yindex]=0;
                        vlast=vlast+f[yindex]-vmax;
                        break;
                    }
                    y[count]=0;
                }
                count++;
            }
            if(count==n){
                break;
            }
        }
        double[][] x=optimizeX(y,s,d,c);
        System.out.println("cost:"+getFunctionValue(y,x,f,c));
        System.out.println("y:");
        for(int j=0;j<n;j++){
            System.out.print(y[j]+" ");
        }
        System.out.println("x:");
        for(int i=0;i<m;i++){
            for(int j=0;j<n;j++){
                System.out.print(x[i][j]+" ");
            }
            System.out.println("");
        }
        return getFunctionValue(y,x,f,c);
    }

    public static void interchangeADDLowerBound(double[] y,double[] s,double[] d,double[] f,double[][] c){
        double[][]xlast=optimizeX(y,s,d,c);
        if(xlast==null){
            System.out.println("It's an infeasible solution, please input again.");
            return;
        }
        int n=y.length;
        int m=d.length;
        double vlast=getFlowValue(xlast,c);


        //按照cost给每个demand排序
        Comparator<node> ct1=new Comparator<node>() {
            @Override
            public int compare(node o1, node o2) {
                return o1.value<o2.value?-1:0;
            }
        };
        node[][] nodes=new node[m][n];
        for(int i=0;i<m;i++){
            for(int j=0;j<n;j++){
                nodes[i][j]=new node(j,c[i][j]);
            }
            Arrays.sort(nodes[i],ct1);
        }
        Comparator<node> ct2=new Comparator<node>() {
            @Override
            public int compare(node o1, node o2) {
                return o1.value>o2.value?-1:0;
            }
        };
        while(true){
            int count=0;
            while(count<n){
                if(y[count]==1){
                    y[count]=0;

                    PriorityQueue<node> lowerBound=new PriorityQueue<node>(ct2);
                    for(int j=0;j<n;j++){
                        if(y[j]==1)
                            continue;
                        double[] k=new double[m];
                        for(int i=0;i<m;i++){
                            if(nodes[i][0].index==j){
                                k[i]=c[i][nodes[i][1].index]-c[i][j];
                            }else
                                k[i]=0;
                        }
                        //use tool to solve the LBS
                        double lbs=getLowerBound(k,s[j],d);
                        lowerBound.add(new node(j,lbs));
                    }

                    double vmax=-Double.MAX_VALUE;
                    int yindex=-1;
                    while(!lowerBound.isEmpty()){
                        node t=lowerBound.poll();
                        if(t.value<vmax)
                            break;
                        int j=t.index;
                        y[j]=1;
                        double[][] x=optimizeX(y,s,d,c);
                        if(x==null)
                            continue;
                        double v=vlast-getFlowValue(x,c)-f[j];
                        if ( v > vmax) {
                            vmax = v;
                            yindex=j;
                        }
                        y[j]=0;
                    }
                    if (vmax<=0)//可变少不可变多！！！！！！！！！！！！
                        break;
                    if(yindex!=count){
                        y[yindex]=1;
                        vlast=vlast-f[yindex]-vmax;
                        break;
                    }
                    y[count]=1;
                }
                count++;
            }
            if(count==n){
                break;
            }
        }
        double[][] x=optimizeX(y,s,d,c);
        System.out.println("cost:"+getFunctionValue(y,x,f,c));
        System.out.println("y:");
        for(int j=0;j<n;j++){
            System.out.print(y[j]+" ");
        }
        System.out.println("x:");
        for(int i=0;i<m;i++){
            for(int j=0;j<n;j++){
                System.out.print(x[i][j]+" ");
            }
            System.out.println("");
        }
    }

}
class  node{
    int index;
    double value;
    public node(int index,double value){
        this.index=index;
        this.value=value;
    }
}
