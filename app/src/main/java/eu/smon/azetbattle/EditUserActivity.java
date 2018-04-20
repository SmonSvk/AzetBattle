package eu.smon.azetbattle;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class EditUserActivity extends AppCompatActivity {

    Button backBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_user);

        backBtn = (Button)findViewById(R.id.backBtn);
    }

    public void onButtonClick(View id) {

        if (id.getId() == R.id.backBtn) {
            startActivity(new Intent(getApplicationContext(), MainAppActivity.class));
        }
        else if (id.getId() == R.id.confirmBtn) {
            // TO DO
        }
    }
}
