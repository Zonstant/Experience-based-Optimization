import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

public class EAMLS{
    public static void main(String[] args) {
        //!!!!!!!!!!!!!!!!!!!重复点的筛除！！！！！！！！！！！！


//!!!!!!!!这一版没有hashset!!!!!!!!!!!!!!!!!!!!!!!!


        int m=0;//j
        int n=0;//i
        int[] f=new int[m];//fj
        int[] h=new int[n];//hi
        int[][] c=new int[n][m];//cij
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
        //read simple SFLP input
        int[] gen={1,2,4,8,16,64,100};
        for(int t=3;t<=32;t++){
            for(int g:gen){
            
        try{
            BufferedReader re=new BufferedReader(new FileReader(new File("小/"+t+"34ChessS.txt")));
            re.readLine();
            String s=re.readLine();
            int[] v= getStringValues(s);
            m=v[0];
            n=v[1];
            f=new int[m];
            h=new int[n];
            c=new int[n][m];
            for(int j=0;j<m;j++){
                s=re.readLine();
                v= getStringValues(s);
                f[j]=v[1];
                for(int i=0;i<n;i++){
                    c[i][j]=v[i+2];
                }
            }
            for(int i=0;i<n;i++){
                h[i]=1;
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        //configuration
        int generation=g;
        int size=m;
        int pInitial=2;
        double pMutation=0.3;
        float beta=(float)0.6;
        int step=m/3;
        long time=System.currentTimeMillis();

        int[][] cSorted=new int[n][m];
        for (int i=0;i<n;i++){
            int[][] nodes=new int[m][2];
            for(int j=0;j<m;j++){
                nodes[j][0]=c[i][j];
                nodes[j][1]=j;
            }
            Arrays.sort(nodes,(a,b)->a[0]-b[0]);
            for(int j=0;j<m;j++){
                cSorted[i][j]=nodes[j][1];
            }
        }
        init[] pop=getInitial(m, size, pInitial, f, h,c,cSorted);
        ArrayList<init> allNeighborInds=new ArrayList<>();
        int count=0;
        while(count<generation){
            init[] off=mutation(pop,pMutation,f,h,c,cSorted);
            ArrayList<init> offls=memoryLocalSearch(pop,off,n,f,h,c,cSorted);
            init[] all=new init[pop.length+off.length+offls.size()];
            System.arraycopy(pop, 0, all, 0, pop.length);
            System.arraycopy(off, 0, all, pop.length, off.length);
            for(int i=pop.length+off.length;i<all.length;i++){
                all[i]=offls.get(i-pop.length-off.length);
            }
            pop=selection(all,size);
            float l3Value=0;
            for(int i=0;i<size;i++){
                boolean in=true;
                int[] x=all[i].x;
                for(init nei:allNeighborInds){
                    for(int j=0;j<m;j++){
                        if(x[j]!=nei.x[j])
                            break;
                        if(j==m-1)
                            in =true;
                    }
                    if(in){
                        break;
                    }
                }
                if(in)
                    l3Value+=1;
            }
            l3Value/=allNeighborInds.size();
            if(l3Value>beta){
                size+=step;
            }
            allNeighborInds.addAll(offls);
            count+=1;
        }
        System.out.println("generation: "+generation);
        System.out.println("time: "+((System.currentTimeMillis()-time)/1000)+"s");
        init ans=selection(pop, 1)[0];
        System.out.println(ans.value);
        for (int j=0;j<m;j++){
            System.out.print(ans.x[j]+" ");
        }
        // System.out.println("");
        int[][] y=getY(ans.x,cSorted);
        // for(int i=0;i<n;i++){
        //     for(int j=0;j<m;j++){
        //         System.out.print(y[i][j]+" ");
        //     }
        //     System.out.println("");
        // }
        try {
        BufferedWriter wr = new BufferedWriter(new FileWriter(new File("小/"+t+"34solution"+g+".txt")));
        String s="";
        for(int i=0;i<n;i++){
            for(int j=0;j<m;j++){
                if(y[i][j]==1){
                    s=s.concat((j+1)+" ");
                    break;
                }
                if(j==m-1)
                    s=s.concat("-1 ");
            }
        }
        wr.write(s);
        wr.write(ans.value);
        wr.flush();
        wr.newLine();
        wr.write("generation: "+generation);
        wr.newLine();
        wr.write("time: "+((System.currentTimeMillis()-time)/1000)+"s");
        wr.flush();
        wr.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    }
    }

//备份
    // public static void main(String[] args) {
    //     //!!!!!!!!!!!!!!!!!!!重复点的筛除！！！！！！！！！！！！
    //     int m=0;//j
    //     int n=0;//i
    //     int[] f=new int[m];//fj
    //     int[] h=new int[n];//hi
    //     int[][] c=new int[n][m];//cij
    //     //read input.txt
    //     // try {
    //     //     BufferedReader re = new BufferedReader(new FileReader(new File("case2.txt")));
    //     //     String s = re.readLine();
    //     //     int[] v= getStringValues(s);
    //     //     m=v[0];
    //     //     n=v[1];
    //     //     f=new int[m];
    //     //     h=new int[n];
    //     //     c=new int[n][m];
    //     //     s = re.readLine();
    //     //     for(int i=0;i<n;i++){
    //     //         s = re.readLine();
    //     //     }
    //     //     s = re.readLine();
    //     //     v= getStringValues(s);
    //     //     for(int j=0;j<m;j++){
    //     //         f[j]=v[j];
    //     //     }
    //     //     for(int i=0;i<n;i++){
    //     //         s = re.readLine();
    //     //         v= getStringValues(s);
    //     //         for(int j=0;j<m;j++){
    //     //             c[i][j]=v[j];
    //     //         }
    //     //     }
    //     //     s = re.readLine();
    //     //     v= getStringValues(s);
    //     //     for(int i=0;i<n;i++){
    //     //         h[i]=v[i];
    //     //     }
    //     // } catch (FileNotFoundException e) {
    //     //     e.printStackTrace();
    //     // } catch (IOException e) {
    //     //     e.printStackTrace();
    //     // }
    //     //read simple SFLP input
    //     try{
    //         BufferedReader re=new 
    //     }

    //     //configuration
    //     int generation=100;
    //     int size=m;
    //     int pInitial=2;
    //     double pMutation=0.3;
    //     float beta=(float)0.6;
    //     int step=m/3;
    //     long time=System.currentTimeMillis();

    //     int[][] cSorted=new int[n][m];
    //     for (int i=0;i<n;i++){
    //         int[][] nodes=new int[m][2];
    //         for(int j=0;j<m;j++){
    //             nodes[j][0]=c[i][j];
    //             nodes[j][1]=j;
    //         }
    //         Arrays.sort(nodes,(a,b)->a[0]-b[0]);
    //         for(int j=0;j<m;j++){
    //             cSorted[i][j]=nodes[j][1];
    //         }
    //     }
    //     init[] pop=getInitial(m, size, pInitial, f, h,c,cSorted);
    //     ArrayList<init> allNeighborInds=new ArrayList<>();
    //     int count=0;
    //     while(count<generation){
    //         init[] off=mutation(pop,pMutation,f,h,c,cSorted);
    //         ArrayList<init> offls=memoryLocalSearch(pop,off,n,f,h,c,cSorted);
    //         init[] all=new init[pop.length+off.length+offls.size()];
    //         System.arraycopy(pop, 0, all, 0, pop.length);
    //         System.arraycopy(off, 0, all, pop.length, off.length);
    //         for(int i=pop.length+off.length;i<all.length;i++){
    //             all[i]=offls.get(i-pop.length-off.length);
    //         }
    //         pop=selection(all,size);
    //         float l3Value=0;
    //         for(int i=0;i<size;i++){
    //             boolean in=true;
    //             int[] x=all[i].x;
    //             for(init nei:allNeighborInds){
    //                 for(int j=0;j<m;j++){
    //                     if(x[j]!=nei.x[j])
    //                         break;
    //                     if(j==m-1)
    //                         in =true;
    //                 }
    //                 if(in){
    //                     break;
    //                 }
    //             }
    //             if(in)
    //                 l3Value+=1;
    //         }
    //         l3Value/=allNeighborInds.size();
    //         if(l3Value>beta){
    //             size+=step;
    //         }
    //         allNeighborInds.addAll(offls);
    //         count+=1;
    //     }
    //     System.out.println("generation: "+generation);
    //     System.out.println("time: "+((System.currentTimeMillis()-time)/1000)+"s");
    //     init ans=selection(pop, 1)[0];
    //     System.out.println(ans.value);
    //     for (int j=0;j<m;j++){
    //         System.out.print(ans.x[j]+" ");
    //     }
    //     // System.out.println("");
    //     // int[][] y=getY(ans.x,cSorted);
    //     // for(int i=0;i<n;i++){
    //     //     for(int j=0;j<m;j++){
    //     //         System.out.print(y[i][j]+" ");
    //     //     }
    //     //     System.out.println("");
    //     // }
    // }    
    public static int[] getStringValues(String s){
        String[] ss=s.split(" ");
        ArrayList<Integer> values=new ArrayList<Integer>();
        for(int i=0;i<ss.length;i++){
            if(!ss[i].equals(""))
                values.add(Integer.valueOf(ss[i]));
        }
        int[] ans=new int[values.size()];
        for(int i=0;i<values.size();i++){
            ans[i]=values.get(i);
        }
        return ans;
    }
    public static int[][] getY(int[] x,int[][] cSorted){
        int m=cSorted[0].length;
        int n=cSorted.length;
        int[][] y=new int[n][m];
        for(int i=0;i<n;i++){
            for(int j=0;j<m;j++){
                int index=cSorted[i][j];
                if(x[index]==1){
                    y[i][index]=1;
                    break;
                }
            }
        }
        return y;
    }
    public static int functionValue(int[] x,int[][] y,int[] f,int[] h,int[][] c){
        boolean fail=true;
        for(int j=0;j<x.length;j++){
            if(x[j]==1){
                fail=false;
                break;
            }
        }
        if(fail)
            return Integer.MAX_VALUE;
        int ans=0;
        for(int j=0;j<x.length;j++){
            ans+=x[j]*f[j];
        }
        for(int i=0;i<h.length;i++){
            for(int j=0;j<x.length;j++){
                ans+=y[i][j]*c[i][j]*h[i];
            }
        }
        return ans;
    }

    public static init[] getInitial(int m, int size, int p,int[] f,int[] h,int[][] c,int[][] cSorted){
        // about 1/p is 1, others is 0
        init[] pop=new init[size];
        Random ra=new Random();
        for(int i=0;i<size;i++){
            int[] x=new int[m];
            for(int j=0;j<m;j++){
                if(ra.nextInt(p)==0)
                    x[j]=1;
            }
            pop[i]=new init(x,functionValue(x, getY(x,cSorted), f, h, c));
        }
        return pop;
    }
    public static ArrayList<init> getNeighbors(init a,int[] f,int[] h,int[][] c,int[][] cSorted){
        ArrayList<init> neighbors=new ArrayList<>();
        for(int j=0;j<a.x.length;j++){
            int[] newX=a.x.clone();
            newX[j]=1-newX[j];
            neighbors.add(new init(newX,functionValue(newX, getY(newX,cSorted), f, h, c)));
        }
        return neighbors;
    }
    static ArrayList<init> inLSed=new ArrayList<>();
    public static ArrayList<init> memoryLocalSearch(init[] pop,init[] off,int l,int[] f,int[] h,int[][] c,int[][] cSorted){
        ArrayList<init> offls=new ArrayList<>();
        init[] all=new init[pop.length+off.length];
        System.arraycopy(pop, 0, all, 0, pop.length);
        System.arraycopy(off, 0, all, pop.length, off.length);
        Arrays.sort(all,(a,b)->a.value-b.value);
        int count=0;
        int m=pop[0].x.length;
        for(int i=0;i<all.length;i++){
            int[] x=all[i].x;
            boolean in=false;
            for(init a:inLSed){
                for(int j=0;j<m;j++){
                    if(x[j]!=a.x[j])
                        break;
                    if(j==m-1){
                        in=true;
                    }
                }
                if(in)
                    break;
            }
            if(in)
                continue;
            ArrayList<init> neighbors=getNeighbors(all[i],f,h,c,cSorted);
            offls.addAll(neighbors);
            inLSed.addAll(neighbors);
            count+=1;
            if(count==l)
                break;
        }
        return offls;
    }
    public static init[] mutation(init[] pop,double p,int[] f,int[] h,int[][] c,int[][] cSorted){
        init[] off=new init[pop.length];
        for(int i=0;i<pop.length;i++){
            int[] newX=pop[i].x.clone();
            for(int j=0;j<newX.length;j++){
                if(Math.random()<=p)
                    newX[j]=1-newX[j];
            }
            off[i]=new init(newX,functionValue(newX, getY(newX,cSorted), f, h, c));
        }
        return off;
    }
    public static init[] selection(init[] pop,int l){
        init[] children=new init[l];
        Arrays.sort(pop,(a,b)->a.value-b.value);
        for(int i=0;i<l;i++){
            children[i]=pop[i];
        }
        return children;
    }
}
class init{
    int[] x;
    int value;
    public init(int[] x,int value){
        this.x=x;
        this.value=value;
    }
}
