package com.example.aniru.a4;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;

import static java.lang.Thread.sleep;

public class MainActivity extends AppCompatActivity {

    TextView t_user1 = null;
    TextView t_user2 = null;

    ListView lv1;
    ListView lv2;

    ArrayList<String>  arr1 = new ArrayList<>();
    ArrayList<String>  arr2 = new ArrayList<>();

    ArrayAdapter<String> itemsAdapter1;
    ArrayAdapter<String> itemsAdapter2;

    // String str1 = "";
    // String str2 = "";

    String guess1 = "";

    Thread t1;
    Thread t2;

    String result2 = "";


    HashMap<String,Integer> u1 = new HashMap<>();
    HashMap<String,Integer> u2 = new HashMap<>();

    public static final int CONST = 50 ;

    private Handler mHandler = new Handler() ;
    public Handler user1;
    public Handler user2;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button start = findViewById(R.id.button_start);



        t_user1 = findViewById(R.id.no_display1);
        t_user2 = findViewById(R.id.no_display2);

        t_user1.setText("User 1 Number");
        t_user2.setText("User 2 Number");

        lv1 = (ListView) findViewById(R.id.list1);
        lv2 = (ListView) findViewById(R.id.list2);
        itemsAdapter1 = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, arr1);
        itemsAdapter2 = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, arr2);


        lv1.setAdapter(itemsAdapter1);
        lv2.setAdapter(itemsAdapter2);


        start.setOnClickListener(new View.OnClickListener() { // Setting up listener for the button
            @Override
            public void onClick(View view)
            {
                if(t1!=null &&  t2!=null)
                {
                    user1.getLooper().quit();
                    user2.getLooper().quit();
                   // t1.interrupt();
                   // t2.interrupt();
                    t1 = null;
                    t2 = null;

                    arr1.clear();
                    arr2.clear();

                    itemsAdapter1.notifyDataSetChanged();
                    itemsAdapter2.notifyDataSetChanged();

                    u1.clear();
                    u2.clear();


                }


                t1 = new Thread(new User1());
                t1.start();

                t2 = new Thread(new User2());
                t2.start();

            }
        });



    }


    class User1 extends Thread
    {
        Message msg1 = new Message();
        String g = "";
        String guess = "";
        String str1="";
        String res = "";
        int stop1 = 0;
        int stop = 0;
        String m="";
        HashMap<String,Integer> var = new HashMap<>();


        public void run()
        {

            final ArrayList<Integer> A = new ArrayList(Arrays.asList(0,1,2,3,4,5,6,7,8,9));

            Collections.shuffle(A);

            for(int i=0;i<4;i++)
            {
                str1 = str1 + A.get(i); // Number is being randomly chosen for user 1
            }

            Collections.shuffle(A);

            for(int i=0;i<4;i++)
            {
                guess = guess + A.get(i); // USER 1 STRATEGY : To have the first guess Random
            }

            mHandler.post(new Runnable() {
                public void run()
                {
                    t_user1.setText(str1); // Displaying the number on the UI

                }
            } ) ;


            for(int i = 0;i<guess.length();i++)
            {
                var.put(str1.charAt(i)+"",i);
            }

            while(user2 == null)
            {

            }

            Bundle b = new Bundle();
            b.putString("res",guess);
            b.putBoolean("indicator",true);
            msg1.setData(b);

            user2.sendMessage(msg1) ;

            msg1 = null;

            Looper.prepare();
            user1 = new Handler() {
                public void handleMessage(Message msg) {

                    String result1 = "";


                    res = msg.getData().getString("res");
                    Boolean flag = msg.getData().getBoolean("indicator");
                    stop = msg.getData().getInt("stop");
                    m = msg.getData().getString("msg");
                    HashMap<Integer, String> r = new HashMap<>();
                    if (stop != 1 && res != null) {


                        if (flag == false) {
                            try {
                                Thread.sleep(2000);
                            } catch (Exception e) {

                            }
                            int count = 0;
                            if (g.length() != 0) {

                                guess = g;
                                g = "";
                            }
                            HashMap<String, Integer> temp = new HashMap<>();

                            for (int i = 0; i < res.length(); i++) {
                                if (res.charAt(i) == '6')  // if incorrect, remove it from possible options
                                {
                                    A.remove(new Integer(Integer.parseInt(guess.charAt(i) + "")));
                                }
                            }
                            for (int i = 0; i < res.length(); i++) {
                                if (res.charAt(i) == '5')  // If in correct position, remove it from possible choices but add it to guess
                                {
                                    r.put(i, guess.charAt(i) + "");
                                    A.remove(new Integer(Integer.parseInt(guess.charAt(i) + "")));

                                } else if (res.charAt(i) != '6')  // if guess was correct but in incorrect position, add it in the right position with correct position being the key
                                {
                                    r.put(Integer.parseInt(res.charAt(i) + ""), guess.charAt(i) + "");
                                    A.remove(new Integer(Integer.parseInt(guess.charAt(i) + "")));
                                }
                            }
                            Log.i("HH", r + "");

                            for (int i = 0; i < res.length(); i++) {
                                if (!r.containsKey(i)) {
                                    Collections.shuffle(A); // go for a new guess for those incorrect guesses

                                    while (temp.containsKey(A.get(0) + ""))
                                    {
                                        Collections.shuffle(A);
                                    }
                                    r.put(i, A.get(0) + "");
                                    temp.put(A.get(0) + "", 1);
                                }
                            }

                            for (String values : r.values())  // Figuring out the guesses and adding them one by one.
                            {
                                g = g + values + "";
                                Log.i("GG", g);
                            }

                            if (res.equals("5555")) { // If the guess is correct


                                stop1 = 1;

                                mHandler.post(new Runnable() {
                                    @Override
                                    public void run()
                                    {
                                        if(res!=null) {

                                            arr1.add("guess " + guess);
                                            arr1.add(m+"("+res+")");
                                            itemsAdapter1.notifyDataSetChanged();

                                            arr1.add("WINNER IS USER 1"); // Declare Winner
                                            Toast.makeText(getApplicationContext(), "USER 1 WINS", Toast.LENGTH_SHORT).show();

                                            msg1 = new Message();

                                            Bundle b = new Bundle();
                                            b.putInt("stop", 1);

                                            msg1.setData(b);

                                            user2.sendMessage(msg1); // inform user 2 from UI thread

                                        }

                                    }
                                });
                            } else {
                                mHandler.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        arr1.add("guess " + guess); // If not, make a new guess
                                        arr1.add(m+"("+res+")");


                                        itemsAdapter1.notifyDataSetChanged();


                                    }
                                });


                                Log.i("WW", "T-1 After stop = 1");
                                msg1 = new Message();

                                Bundle b = new Bundle();
                                b.putString("res", g);
                                b.putBoolean("indicator", true); // true for guess, and false for response


                                msg1.setData(b);


                                user2.sendMessage(msg1);


                            }
                        } else if (flag == true) { // If incoming guess


                            int count_true = 0;
                            int count_pos = 0;
                            int incorr = 0;


                            for (int i = 0; i < 4; i++) {
                                if (res.charAt(i) == str1.charAt(i)) {
                                    result1 = result1 + "5"; // count  correc digit guesses
                                    count_true++;
                                } else {
                                    if (var.containsKey(res.charAt(i) + "")) { // count misplaced guesses
                                        count_pos++;
                                        result1 = result1 + var.get(res.charAt(i) + "");

                                    } else {
                                        incorr++;
                                        result1 = result1 + "6"; // count incorrect guesses
                                    }

                                }
                            }


                            String feedback = "Correct position : " + count_true + "\nincorrect position : " + count_pos + "\nincorrect number = " + incorr;
                            Bundle b = new Bundle();
                            b.putBoolean("indicator", false);
                            b.putString("res", result1);
                            b.putString("msg", feedback);

                            msg1 = new Message();
                            msg1.setData(b);
                            if (stop1 != 1) {

                                user2.sendMessage(msg1); // send a feedback to the user about the guess
                                msg1 = null;
                            }
                        }


                    }
                }
            };
            Looper.loop();


        }
    }

    class User2 implements Runnable
    {
        Message msg2;
        String guess2 = "";
        String g = "";
        String guess = "1379"; // Guess STRATEGY of user 2 is to have a unifrom distribution of digits starting off similar to a binary search
        String str2 = "";
        String res = "";
        String guess1 = "1379";
        String m="";

        int used = 0;
        int stop = 0;
        int stop1 = 0;

        HashMap<String,Integer> var = new HashMap<>(); // A list to keep track of those variables that have been used

        public void run()
        {
            final ArrayList<Integer> A = new ArrayList(Arrays.asList(0,1,2,3,4,5,6,7,8,9));

            Collections.shuffle(A);
            String str = "";

            for(int i=0;i<4;i++)
            {
                str2 = str2+ A.get(9-i); // Chooses different set of random numbers, last 4 digits
            }
            for(int i = 0;i<guess.length();i++)
            {
                var.put(str2.charAt(i)+"",i);
            }

            mHandler.post(new Runnable() {
                public void run()
                {
                    t_user2.setText(str2+""); // Informing the UI thread of the choice of number
                }
            } ) ;

            //Log.i("CCC","check");
            Looper.prepare();
            user2 = new Handler()
            {
                public void handleMessage(Message msg)
                {
                    while(user1 == null)
                    {

                    }
                    result2 = "";
                    guess2 = msg.getData().getString("res");
                    res = guess2;
                    stop = msg.getData().getInt("stop");
                    m = msg.getData().getString("msg");
                    Boolean indicator = msg.getData().getBoolean("indicator"); // getting an indication of what kind of message it is
                    if(stop == 1)
                    {
                        Log.i("STOP",stop+"");

                    }
                    if(res!=null    &&  stop!=1) {
                        HashMap<Integer, String> r = new HashMap<>();

                        if (indicator == false) {

                            if (g.length() != 0) {

                                guess = g;
                                g = "";
                            }


                            for(int i=0;i<res.length();i++)
                            {
                                if(res.charAt(i) == '6') // if incorrect guess, remove from possible guesses
                                {
                                    A.remove(new Integer(Integer.parseInt(guess.charAt(i) + "")));
                                }
                            }

                            for(int i=0;i<res.length();i++)
                            {
                                if(res.charAt(i) == '5') // correct guess
                                {
                                    r.put(i,guess.charAt(i)+"");
                                    A.remove(new Integer(Integer.parseInt(guess.charAt(i) + "")));

                                }

                                else if(res.charAt(i)!='6') // incorrect guess
                                {
                                    r.put(Integer.parseInt(res.charAt(i)+""),guess.charAt(i)+"");
                                    A.remove(new Integer(Integer.parseInt(guess.charAt(i) + "")));
                                }
                            }
                            Log.i("HH",r+"");
                            HashMap<String, Integer> temp = new HashMap<>();
                            for(int i=0;i<res.length();i++)
                            {
                                if(!r.containsKey(i))
                                {
                                    Collections.shuffle(A);

                                    while (temp.containsKey(A.get(0)+"") )
                                    {
                                        Collections.shuffle(A);
                                    }
                                    r.put(i,A.get(0)+"");
                                    temp.put(A.get(0)+"",1);
                                }
                            }

                            for (String values : r.values()) // add up values
                            {
                                g = g+values+"";
                                Log.i("GG",g);
                            }
                            if (res.equals("5555"))
                            {


                                stop1 = 1;
                                Toast.makeText(getApplicationContext(), "Successful " + g, Toast.LENGTH_SHORT).show();
                                mHandler.post(new Runnable() {
                                    @Override
                                    public void run()
                                    {
                                        arr2.add("guess "+guess);
                                        arr2.add(m+"("+res+")");
                                        itemsAdapter2.notifyDataSetChanged();

                                        arr2.add("WINNER IS USER 2");
                                        Toast.makeText(getApplicationContext(),"USER 2 WINS",Toast.LENGTH_SHORT).show();

                                        msg2 = new Message();

                                        Bundle b = new Bundle();
                                        b.putInt("stop", 1);

                                        msg2.setData(b);

                                        user1.sendMessage(msg2);


                                    }
                                });
                            } else {

                                //Toast.makeText(getApplicationContext(),"2-" + g, Toast.LENGTH_SHORT).show();
                                mHandler.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        if (stop != 1) {
                                            arr2.add("guess " + guess);
                                            arr2.add(m+"("+res+")");

                                            itemsAdapter2.notifyDataSetChanged();
                                        }
                                    }
                                });
                                msg2 = new Message();

                                Bundle b = new Bundle();
                                b.putString("res", g);
                                b.putBoolean("indicator", true); // true for guess, and false for response


                                msg2.setData(b);

                                try {
                                    Thread.sleep(3000);
                                } catch (Exception e) {

                                }

                                if (stop1 != 1) {

                                    user1.sendMessage(msg2);
                                }


                            }
                        }
                        if (indicator == true) {
                            int count_true = 0;
                            int count_pos = 0;
                            int incorr = 0;


                            for (int i = 0; i < 4; i++) {
                                if (guess2.charAt(i) == str2.charAt(i)) { // counting number of correct position guesses
                                    result2 = result2 + "5";
                                    count_true++;
                                } else {
                                    if (var.containsKey(guess2.charAt(i) + "")) { // counting misplaced position
                                        count_pos++;
                                        result2 = result2 + var.get(guess2.charAt(i) + "");

                                    } else {
                                        incorr++; // counting incorrect guesses
                                        result2 = result2 + "6";
                                    }

                                }
                            }


                            String feedback = "Correct position : " + count_true + "\nincorrect position : " + count_pos + "\nincorrect = " + incorr;
                            Bundle b = new Bundle();
                            b.putBoolean("indicator", false);
                            b.putString("res", result2);
                            b.putString("msg", feedback);

                            msg2 = new Message();
                            msg2.setData(b);


                            user1.sendMessage(msg2);
                            msg2 = null;

                            if (used == 0) {
                                msg2 = new Message();

                                Bundle b1 = new Bundle();
                                b1.putString("res", guess);
                                b1.putBoolean("indicator", true); // true for guess, and false for response


                                msg2.setData(b1);

                                try {
                                    Thread.sleep(3000);
                                } catch (Exception e) {

                                }


                                user1.sendMessage(msg2);
                                used = 1;

                            }
                        }
                    }

                }

            };
            Looper.loop();
           /* Message msg = user1.obtainMessage(CONST) ;
            msg.arg1 = 0 ;
            user1.sendMessage(msg) ;*/


        }
    }
}