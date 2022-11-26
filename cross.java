import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Random;

public class cross{

    public static double[] getStringValues(String s){
        String[] ss=s.split(" ");
        ArrayList<Double> values=new ArrayList<Double>();
        for(int i=0;i<ss.length;i++){
            if(!ss[i].equals(""))
                values.add(Double.valueOf(ss[i]));
        }
        double[] ans=new double[values.size()];
        for(int i=0;i<values.size();i++){
            ans[i]=values.get(i);
        }
        return ans;
    }

    public static void readRandomInput(){
        //read input.txt
        // try {
        //     BufferedReader re = new BufferedReader(new FileReader(new File("case2.txt")));
        //     String s = re.readLine();
        //     int[] v= getStringValues(s);
        //     m=v[0];
        //     n=v[1];
        //     f=new int[m];
        //     h=new int[n];
        //     c=new int[n][m];
        //     s = re.readLine();
        //     for(int i=0;i<n;i++){
        //         s = re.readLine();
        //     }
        //     s = re.readLine();
        //     v= getStringValues(s);
        //     for(int j=0;j<m;j++){
        //         f[j]=v[j];
        //     }
        //     for(int i=0;i<n;i++){
        //         s = re.readLine();
        //         v= getStringValues(s);
        //         for(int j=0;j<m;j++){
        //             c[i][j]=v[j];
        //         }
        //     }
        //     s = re.readLine();
        //     v= getStringValues(s);
        //     for(int i=0;i<n;i++){
        //         h[i]=v[i];
        //     }
        // } catch (FileNotFoundException e) {
        //     e.printStackTrace();
        // } catch (IOException e) {
        //     e.printStackTrace();
        // }
    }

    public static void readSimple(String file,ArrayList f,ArrayList h,ArrayList c){
        try{
            BufferedReader re=new BufferedReader(new FileReader(new File(file)));
            re.readLine();
            String s=re.readLine();
            double[] v= getStringValues(s);
            int m=(int)v[0];
            int n=(int)v[1];
            double [][] cc=new double[n][m];
            for(int j=0;j<m;j++){
                s=re.readLine();
                v= getStringValues(s);
                f.add(v[1]);
                for(int i=0;i<n;i++){
                    cc[i][j]=v[i+2];
                }
            }
            for(int i=0;i<n;i++){
                h.add((double)1.0);
            }
            for(int i=0;i<n;i++){
                c.add(cc[i]);
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void readORLIB(String file,ArrayList f,ArrayList h,ArrayList c){
        try{
            BufferedReader re=new BufferedReader(new FileReader(new File(file)));
            String s=re.readLine();
            double[] v= getStringValues(s);
            int m=(int)v[0];
            int n=(int)v[1];
            for(int j=0;j<m;j++){
                s=re.readLine();
                v= getStringValues(s);
                f.add(v[1]);
            }
            for(int i=0;i<n;i++){
                v=getStringValues(re.readLine());
                h.add(v[0]);
                double[] cc=new double[m];
                v=getStringValues(re.readLine());
                for(int j=0;j<m;j++){
                    cc[j]=v[j];
                }
                c.add(cc);
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args){
        BufferedWriter solution=null;
        try {
            solution=new BufferedWriter(new FileWriter(new File("cross.txt")));
        } catch (IOException e) {
            e.printStackTrace();
        }
        ArrayList<String> files=new ArrayList<String>();
        try {
            for (File s:new File("M/R").listFiles()){
                if(!s.getCanonicalPath().contains("."))
                    files.add(s.getCanonicalPath());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        for(String file :files){      
            ArrayList<Double> ff=new ArrayList<Double>();
            ArrayList<Double> hh=new ArrayList<Double>();
            ArrayList<double[]> cc=new ArrayList<double[]>();
            readORLIB(file,ff,hh,cc);
            int m=ff.size();//j
            int n=hh.size();//i
            double[] f=new double[m];//fj
            for(int j=0;j<m;j++){
                f[j]=ff.get(j);
            }
            double[] h=new double[n];//hi
            for(int i=0;i<n;i++){
                h[i]=hh.get(i);
            }
            double[][] c=new double[n][m];//cij 
            for(int i=0;i<n;i++){
                c[i]=cc.get(i);
            }
            init.f=f;
            init.h=h;
            init.c=c;
            int[][] cSorted=new int[n][m];
            for (int i=0;i<n;i++){
                double[][] nodes=new double[m][2];
                for(int j=0;j<m;j++){
                    nodes[j][0]=c[i][j];
                    nodes[j][1]=j;
                }
                Arrays.sort(nodes,(a,b)->a[0]-b[0]<0?-1:a[0]==b[0]?0:1);
                for(int j=0;j<m;j++){
                    cSorted[i][j]=(int)nodes[j][1];
                }
            }
            init.cSorted=cSorted;
            // for(int j=0;j<m;j++){
            //     System.out.print(f[j]+" ");
            // }
            // System.out.println();
            // for(int i=0;i<n;i++){
            //     System.out.print(h[i]+" ");

            // }
            // System.out.println();
            // for(int i=0;i<n;i++){
            //     for(int j=0;j<m;j++){
            //         System.out.print(c[i][j]+" ");
            //     }
            //     System.out.println();
            // }

            //configuration
            int generation=300;
            int size=100;
            double mutationRate=0.3;
            float beta=(float)0.75;
            int step=50;
            try {
                solution.write(file+";m:"+m+";n:"+n+";generation:"+generation+";beta:"+beta+"\n");
                solution.write("generation\tans\tl3_value\tsize\tmutate\tMLS\tl3_value\ttotal\n");
            } catch (IOException e) {
                e.printStackTrace();
            }
            init[] pop=getInitial(m, size);
            HashSet<String> allNeighborInds=new HashSet<>();
            int count=0;
            double time=System.currentTimeMillis();
            while(count<generation){
                init[] offc=cross(pop,m);
                init[] off=mutation(pop,mutationRate);
                ArrayList<init> offls=memoryLocalSearch(pop,off,10);
                init[] all=new init[pop.length+off.length+offls.size()+offc.length];
                System.arraycopy(pop, 0, all, 0, pop.length);
                System.arraycopy(off, 0, all, pop.length, off.length);
                for(int i=pop.length+off.length;i<pop.length+off.length+offls.size();i++){
                    all[i]=offls.get(i-pop.length-off.length);
                }
                System.arraycopy(offc,0,all,pop.length+off.length+offls.size(),offc.length);
                pop=selection(all,size);
                float l3Value=0;
                for(int i=0;i<pop.length;i++){
                    String xs=pop[i].xs;
                    if(allNeighborInds.contains(xs))
                        l3Value+=1;
                }
                l3Value/=pop.length;
                if(l3Value>beta){
                    size+=step;
                }
                for(int i=0;i<offls.size();i++){
                    allNeighborInds.add(offls.get(i).xs);
                }
                count+=1;
                try {
                    solution.write("generation:"+count+"  l3Value:"+l3Value+"    ans:"+selection(pop,1)[0].value+"  \n");
                    solution.flush();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            try {
                solution.write("time: "+((System.currentTimeMillis()-time)/1000)+"s\n");
                solution.flush();
            } catch (IOException e) {
                e.printStackTrace();
            }           
        }
        try {
            solution.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static init[] getInitial(int m, int size){
        init[] pop=new init[size];
        Random ra=new Random();
        for(int i=0;i<size;i++){
            int[] x=new int[m];
            for(int j=0;j<m;j++){
                //about 1/2 is 1;
                if(ra.nextInt(2)==0)
                    x[j]=1;
            }
            pop[i]=new init(x);
        }
        return pop;
    }    
    public static init[] cross(init[] pop,int size){
        int m=pop[0].x.length;
        int l=pop.length;
        double[] rs=new double[l];
        double sum=0;
        for(int i=0;i<l;i++){
            sum+=1/pop[i].value;
            rs[i]=sum;
        }
        init[] off=new init[2*size];
        for(int i=0;i<size;i++){
            init a=null;
            double r=Math.random()*sum;
            for(int j=0;j<l;j++){
                if(rs[j]>=r){
                    a=pop[j];
                    break;
                }
            }
            init b=null;
            r=Math.random()*sum;
            for(int j=0;j<l;j++){
                if(rs[j]>=r){
                    b=pop[j];
                    break;
                }
            }

            int mid=(int)(Math.random()*m);
            int[]x1=a.x.clone();
            int[]x2=b.x.clone();
            for(int j=mid;j<m;j++){
                x1[j]=b.x[j];
                x2[j]=a.x[j];
            }
            off[2*i]=new init(x1);
            off[2*i+1]=new init(x2);
        }
        return off;
    }

    public static init[] mutation(init[] pop,double p){
        init[] off=new init[pop.length];
        for(int i=0;i<pop.length;i++){
            int[] newX=pop[i].x.clone();
            for(int j=0;j<newX.length;j++){
                if(Math.random()<=p)
                    newX[j]=1-newX[j];
            }
            off[i]=new init(newX);
        }
        return off;
    }    
    public static init[] selection(init[] pop,int size){
        HashSet<String> set=new HashSet<>();
        init[] children=new init[size];
        Arrays.sort(pop,(a,b)->a.value-b.value<0?-1:a.value==b.value?0:1);
        int count=0;
        int index=0;
        int l=pop.length;
        while((count<size)&&(index<l)){
            if(!set.contains(pop[index].xs)){
                children[count]=pop[index];
                set.add(pop[index].xs);
                count++;
            }
            index++;
        }
        return children;
    }

    public static ArrayList<init> getNeighbors(init a){
        ArrayList<init> neighbors=new ArrayList<>();
        for(int j=0;j<a.x.length;j++){
            int[] newX=a.x.clone();
            newX[j]=1-newX[j];
            neighbors.add(new init(newX));
        }
        return neighbors;
    }
    static HashSet<String> inLSed=new HashSet<>();
    public static ArrayList<init> memoryLocalSearch(init[] pop,init[] off,int size){
        ArrayList<init> offls=new ArrayList<>();
        init[] all=new init[pop.length+off.length];
        System.arraycopy(pop, 0, all, 0, pop.length);
        System.arraycopy(off, 0, all, pop.length, off.length);
        Arrays.sort(all,(a,b)->a.value-b.value<0?-1:a.value==b.value?0:1);
        int count=0;
        for(int i=0;i<all.length;i++){
            String xs=all[i].xs;
            if(inLSed.contains(xs))
                continue;
            offls.addAll(getNeighbors(all[i]));
            inLSed.add(xs);
            count++;
            if(count==size)
                break;
        }
        return offls;
    }

}
