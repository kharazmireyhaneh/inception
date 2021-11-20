import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;
import java.util.Scanner;
import java.util.concurrent.Semaphore;

public class KSP {
    static Semaphore mutex = new Semaphore(1);
    static int zaman;
    static int[] Ingrediants = {2,2,2,2,2};
    static int row = 1;
    static int GordonOrd = 0;
    static int OliverOrd = 0;
    static Cheif cheifGordon;
    static Cheif cheifoliver;

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        int n = sc.nextInt();

        cheifGordon = new Cheif("Gordon Ramsay");
        cheifoliver = new Cheif("Jamie Oliver");

        for (int i = 0; i < n ; i++) {
            int ord = sc.nextInt();
            if (ord==1) {
                cheifGordon.consumerNum.add(i+1);
                GordonOrd++;
            }
            else {OliverOrd++;
            cheifoliver.consumerNum.add(i+1);
            }
        }

        Assistant assistant = new Assistant();
        cheifGordon.start();
        cheifoliver.start();
        try {
            Thread.sleep(5);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        assistant.start();
    }

}

class Cheif extends Thread{
    String name;
    ArrayList<Integer> consumerNum = new ArrayList<>();

    //0:Goosht,1:Goje,2:Piaz,3:Ketchup,4:Mustard
    int[] GordonIng = {1,2,1,2,0};
    int[] OliverIng = {2,0,3,2,2};
    int[] CheifIng = new int[5];
    ArrayList<Boolean> needed = new ArrayList<>(5);
    int cnt;

    Cheif(String name){
        this.name = name;
    }
    @Override
    public void run() {
        for (int i = 0; i < 5 ; i++) {
            needed.add(false);
        }
        if (name.equals("Gordon Ramsay")) {
            cnt = KSP.GordonOrd;
            CheifIng = Arrays.copyOf(GordonIng,5);
        }
        else {
            cnt = KSP.OliverOrd;
            CheifIng = Arrays.copyOf(OliverIng,5);
        }
        try {
            while (cnt>0){
                KSP.mutex.acquire();

                //check mojudi
                for (int i = 0; i < 5 ; i++) {
                    if(KSP.Ingrediants[i] >= CheifIng[i])
                        needed.set(i,true);
                }
                if (!needed.contains(false)) {
                    System.out.println(KSP.row + "-" +consumerNum.get(0) + "-" + name + "-" + KSP.zaman);
                    consumerNum.remove(0);
                    if (name.equals("Gordon Ramsay"))
                        KSP.GordonOrd--;
                    else KSP.OliverOrd--;
                    for (int i = 0; i < 5; i++) {
                        KSP.Ingrediants[i] -= CheifIng[i];
                        if(KSP.Ingrediants[i] < CheifIng[i])
                            needed.set(i,false);
                    }
                    KSP.row++;
                    cnt--;
                }

                KSP.mutex.release();
            }
        }
        catch (Exception x){
            x.printStackTrace();
        }

    }

}

class Assistant extends Thread{
    Cheif cheif;
    ArrayList<Integer> randomAdd = new ArrayList<>();
    @Override
    public void run() {
        int last = -1;
        while (true){
            if (KSP.zaman%20 == 0) {
                if (KSP.OliverOrd > KSP.GordonOrd)
                    cheif = KSP.cheifoliver;
                else
                    cheif = KSP.cheifGordon;
            }
            randomAdd.clear();
            for (int i = 0; i < 5 ; i++) {
               if(!cheif.needed.get(i))
               {
                   if (i!=last)
                  randomAdd.add(i);
               }
            }
            if (randomAdd.isEmpty()){
                for (int i = 0; i < 5 ; i++) {
                    if (cheif.CheifIng[i]>0)
                    randomAdd.add(i);
                }
            }
              int rand = random(randomAdd.size());
                rand = randomAdd.get(rand);
                last = rand;
                try {
                    Thread.sleep(100);
                    if (KSP.GordonOrd == 0 && KSP.OliverOrd == 0)
                        break;
                    KSP.mutex.acquire();

                    KSP.Ingrediants[rand] += 5;
                    if (KSP.Ingrediants[rand]>10)
                        KSP.Ingrediants[rand]=10;
                    KSP.zaman ++;
                    KSP.mutex.release();
                }catch (Exception x){
                    x.printStackTrace();
                }

                try {
                    Thread.sleep(100);
                    if (KSP.GordonOrd == 0 && KSP.OliverOrd == 0)
                        break;
                    KSP.mutex.acquire();

                    KSP.Ingrediants[rand] += 5;
                    if (KSP.Ingrediants[rand]>10)
                        KSP.Ingrediants[rand]=10;
                    KSP.zaman ++;
                    KSP.mutex.release();
                }catch (Exception x){
                    x.printStackTrace();
                }

            }

        System.out.println("Goosht " + KSP.Ingrediants[0] + "-Goje " + KSP.Ingrediants[1]
                    +"-Piaz " + KSP.Ingrediants[2] + "-Ketchup " + KSP.Ingrediants[3]
                    +"-Mustard " + KSP.Ingrediants[4]);
    }
static int random(int upperbound){
    Random rand = new Random();
    int random = rand.nextInt(upperbound);
    return random;
}
}