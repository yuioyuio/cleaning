package yuioyuoi.cleaning.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.TextView;

import java.util.Random;

import yuioyuoi.cleaning.R;

public class DisplayMessageActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_message);


        Intent intent = getIntent();
        String message = intent.getStringExtra(Dashboard.EXTRA_MESSAGE);
        TextView textView = (TextView) findViewById( R.id.view_message );
        textView.setTextSize( 40 );
        textView.setText( message );

        Random random = new Random();

        textView = (TextView) findViewById( R.id.random );
        textView.setTextSize( 60 );
        //textView.setText( "random " + random.nextInt() );
        textView.setText( ( (Integer) random.nextInt() ).toString() );

        /*TextView textView = new TextView( this );
        textView.setTextSize( 40 );
        textView.setText( message );
        setContentView( textView );*/
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
}
