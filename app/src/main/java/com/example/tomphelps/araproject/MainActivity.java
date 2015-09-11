package com.example.tomphelps.araproject;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import com.example.tomphelps.araproject.ui_fragments.MenuFragment;
import com.example.tomphelps.araproject.ui_fragments.QuickConfirm;
import com.example.tomphelps.araproject.ui_fragments.QuickResultsFragment;

public class MainActivity extends FragmentActivity {
    String sel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_layout);

        if (findViewById(R.id.fragment_layout) != null) {
            if (savedInstanceState != null)
                return;
            insertFragment(new MenuFragment(), "menu", null);
        }
        sel = null;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    public void quickConfirm(View view) {
        Button button = (Button) view;
        sel = button.getText().toString().toLowerCase();
        System.out.println(sel);
        Bundle args = new Bundle();
        args.putString("param1", sel);
        insertFragment(new QuickConfirm(), "confirm", args);
    }

    public void startTest(View view) {
        if (sel == null) {
            return;
        }
        switch (sel) {
            case "ph":
                //Send commands for ph
                Bundle args = new Bundle();
                args.putString("param1", sel);
                insertFragment(new QuickResultsFragment(), "results", args);
                break;
            case "glucose":
                //Send commands for glucose test
                break;
            case "heavy metal":
                //Send comands for heavy metal test
                break;
            case "amperometry":
                //send commands for amperometry
                break;
            case "eis":
                //set up eis test
                break;
            case "other":
                //Go to other options
                break;

            default:
                break;
        }

    }

    public void returnToMenu(View view) {
        sel = null;
        MenuFragment menu = new MenuFragment();
        insertFragment(menu, "menu", null);
    }

    public void launchTesting(View view) {
        Intent intent = new Intent(this, TestingActivity.class);
        startActivity(intent);
    }

    @Override
    public void onBackPressed() {
        MenuFragment menu = new MenuFragment();
        insertFragment(menu, "menu", null);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    int id;

    public void insertFragment(android.support.v4.app.Fragment fragment, String tag, Bundle args) {
        android.support.v4.app.Fragment checkFragment = getSupportFragmentManager().
                findFragmentByTag(tag);
        if (checkFragment == null) {
            System.out.println("null fragment");
            fragment.setArguments(args);
            android.support.v4.app.FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.fragment_layout, fragment, tag);
            transaction.commit();
        } else {
            android.support.v4.app.FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.fragment_layout, checkFragment, tag);
            transaction.setTransition(android.support.v4.app.FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
            id = transaction.commit();
        }
//        int trycount=0;
//        System.out.println("Ran");
//        if(tag.contains("menu")) {
//            while (getSupportFragmentManager().getBackStackEntryCount() > 0&&trycount<10) {
//                getSupportFragmentManager().popBackStack(tag, FragmentManager.POP_BACK_STACK_INCLUSIVE);
//                trycount++;
//            }
//            System.out.println("Ended loop, stacksize"+ getSupportFragmentManager().getBackStackEntryCount());
//        }
    }
}
