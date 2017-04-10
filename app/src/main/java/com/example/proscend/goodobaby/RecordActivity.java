package com.example.proscend.goodobaby;


        import android.app.Activity;
        import android.app.ListActivity;
        import android.content.Intent;
        import android.os.Bundle;
        import android.view.GestureDetector;
        import android.view.View;
        import android.widget.Button;

/**
 * Created by proscend on 2015/7/23.
 */
public class RecordActivity extends Activity {


    private Button btnBack;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_record);

        setupViewComponent();

    }




    private void setupViewComponent(){
        btnBack = (Button)findViewById(R.id.btnback);
        btnBack.setOnClickListener(btnBackOnClick);



    }


    private Button.OnClickListener btnBackOnClick = new Button.OnClickListener(){
        public void onClick(View v){
            Intent intent = new Intent();
            intent.setClass(RecordActivity.this, com.example.proscend.goodobaby.ListActivity.class);

            startActivity(intent);
            RecordActivity.this.finish();


        }
    };







}
