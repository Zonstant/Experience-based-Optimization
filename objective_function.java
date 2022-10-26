
import java.io.*;
import java.util.ArrayList;
import java.util.Random;

public class objective_function {

    public static void createInput(int m,int n){
        //m for j, n for i
        Random ra=new Random();
        try{
            BufferedWriter wr=new BufferedWriter(new FileWriter(new File("input.txt")));
            wr.write(m+" "+n);
            wr.newLine();
            String s="";
            ArrayList<Integer> x=new ArrayList<>();
            for(int j=0;j<m;j++){
                if(ra.nextInt(3)==0){
                    s=s.concat(1+" ");
                    x.add(j);
                }
                else{
                    s=s.concat(0+" ");
                }
            }
            s=s.substring(0,s.length()-1);
            wr.write(s);
            wr.newLine();
            int xs=x.size();
            int count=0;
            for(int i=0;i<n;i++){
                s="";
                boolean set=false;
                for(int j=0;j<m;j++){
                    if(x.contains(j)){
                        count+=1;
                        if((!set)&&((ra.nextInt(xs)==0)||(count==xs))) {
                            s=s.concat(1 + " ");
                            set=true;
                        }
                        else
                            s=s.concat(0+" ");
                    }else
                        s=s.concat(0+" ");
                }
                s=s.substring(0,s.length()-1);
                wr.write(s);
                wr.newLine();
                wr.flush();
            }
            s="";
            for(int j=0;j<m;j++){
                s=s.concat(ra.nextInt(3000)+1+" ");
            }
            s=s.substring(0,s.length()-1);
            wr.write(s);
            wr.newLine();
            for(int i=0;i<n;i++){
                s="";
                for(int j=0;j<m;j++){
                    s=s.concat(ra.nextInt(10)+1+" ");
                }
                s=s.substring(0,s.length()-1);
                wr.write(s);
                wr.newLine();
                wr.flush();
            }
            s="";
            for(int i=0;i<n;i++){
                s=s.concat(ra.nextInt(30)+1+" ");
            }
            s=s.substring(0,s.length()-1);
            wr.write(s);
            wr.flush();
            wr.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        int[] x;//xj   m
        int[][] y;//yij
        int[] f;//fj
        int[] h;//hi   n
        int[][] c;//cij
        createInput(20,300);
        try {
            BufferedReader re = new BufferedReader(new FileReader(new File("input.txt")));
            String s = re.readLine();
            int[] v= getStringValues(s);
            int m=v[0];
            int n=v[1];
            x=new int[m];
            y=new int[n][m];
            f=new int[m];
            h=new int[n];
            c=new int[n][m];
            s = re.readLine();
            v= getStringValues(s);
            for(int j=0;j<m;j++){
                x[j]=v[j];
            }
            for(int i=0;i<n;i++){
                s = re.readLine();
                v= getStringValues(s);
                for(int j=0;j<m;j++){
                    y[i][j]=v[j];
                }
            }
            s = re.readLine();
            v= getStringValues(s);
            for(int j=0;j<m;j++){
                f[j]=v[j];
            }
            for(int i=0;i<n;i++){
                s = re.readLine();
                v= getStringValues(s);
                for(int j=0;j<m;j++){
                    c[i][j]=v[j];
                }
            }
            s = re.readLine();
            v= getStringValues(s);
            for(int i=0;i<n;i++){
                h[i]=v[i];
            }
            System.out.println(functionValue(x,y,f,h,c));

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

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



    public static int functionValue(int[] x,int[][] y,int[] f,int[] h,int[][] c){
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




}
