package com.example.bbbar.main;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.bbbar.R;
import com.example.bbbar.InputWritterInfo.InputWriterActivity;
import com.example.bbbar.StatisticsInfo.StatisticsIndexActivity;

public class IndexActivity extends Activity {
	private Activity activity=this;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
	    setContentView(R.layout.atv_index);
	    
	    ImageView back=(ImageView)findViewById(R.id.back);
	    back.setVisibility(View.GONE);
	    TextView title=(TextView)findViewById(R.id.title);
	    title.setText(getString(R.string.app_name));
	    
	    GridView gv=(GridView) findViewById(R.id.gv);
	    MyAdapter adapter=new MyAdapter();
	    gv.setAdapter(adapter);
	    gv.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				// TODO Auto-generated method stub
				Intent intent=null;
				int kind=-1;
				switch (position) {
				case 0:
					kind=InputWriterActivity.BUY_INPUT;
					intent=new Intent(activity,InputWriterActivity.class);
					intent.putExtra("kind", kind);
					break;
				case 1:
					kind=InputWriterActivity.SELL_INPUT;
					intent=new Intent(activity,InputWriterActivity.class);
					intent.putExtra("kind", kind);
					break;
				case 2:
					kind=InputWriterActivity.ADD_INPUT;
					intent=new Intent(activity,InputWriterActivity.class);
					intent.putExtra("kind", kind);
					break;
				case 3:
					intent=new Intent(activity,StatisticsIndexActivity.class);		
					break;
				default:
					break;
				}
				startActivity(intent);
			}
		});
	}
	public  class  MyAdapter extends BaseAdapter
	{
		String[] titles={"商品录入","交易录入","补货录入","统计"};
		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return titles.length;
		}

		@Override
		public Object getItem(int position) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			// TODO Auto-generated method stub
			if(convertView==null)
			{
				convertView=getLayoutInflater().inflate(R.layout.grid_item, null,false);
			}
			TextView tv=(TextView) convertView.findViewById(R.id.tv);
			tv.setText(titles[position]);
			return convertView;
		}
		
	}
}
