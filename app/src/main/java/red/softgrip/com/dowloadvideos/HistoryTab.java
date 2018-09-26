package red.softgrip.com.dowloadvideos;

/**
 * Created by Qaiser Pasha on 9/24/2018.
 */

import android.content.Context;
import android.content.Intent;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.Toolbar;

import static android.content.Context.LAYOUT_INFLATER_SERVICE;

public class HistoryTab  extends Fragment {


    GridView Grid;
    GRIDAdapter adapter;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootview = inflater.inflate(R.layout.fragment_history, container, false);
//        Toolbar toolbar = (Toolbar)rootview.findViewById(R.id.toolbar);
////        setSupportActionBar(toolbar);

        final Integer[] image = new Integer[]{R.drawable.image1, R.drawable.image2, R.drawable.image3,
                R.drawable.image4, R.drawable.image5, R.drawable.image6,
                R.drawable.image7, R.drawable.image8};

        Grid = (GridView)rootview.findViewById(R.id.MyGrid);
        adapter = new GRIDAdapter(getContext(), R.layout.grid_item, image);

        Grid.setAdapter(adapter);
        Grid.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
                Intent go = new Intent(getContext(), Details.class);
                go.putExtra("image", image[position]);
                //By position Clicked
                startActivity(go);
            }
        });

        return rootview;
    }

    public class GRIDAdapter extends ArrayAdapter {
        private Integer[] Image;
        private int resource;
        private LayoutInflater inflater;

        public GRIDAdapter(Context context, int resource, Integer[] image) {
            super(context, resource, image);
            Image = image;
            this.resource = resource;
            inflater = (LayoutInflater)getContext().getSystemService(LAYOUT_INFLATER_SERVICE);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            ViewHolder Holder = null;

            if (convertView == null) {
                Holder = new ViewHolder();
                convertView = inflater.inflate(resource, null);
                Holder.IMAGE = (ImageView) convertView.findViewById(R.id.imageID);
                convertView.setTag(Holder);
            } else {
                Holder = (ViewHolder)convertView.getTag();
            }
            Holder.IMAGE.setImageResource(Image[position]);
            Holder.IMAGE.setScaleType(ImageView.ScaleType.CENTER_CROP);

            return convertView;
        }

        class ViewHolder {
            private ImageView IMAGE;
        }
    }

//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        // Inflate the menu; this adds items to the action bar if it is present.
//        getMenuInflater().inflate(R.menu.menu_main, menu);
//        return true;
//    }

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
