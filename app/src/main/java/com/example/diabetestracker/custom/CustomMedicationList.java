package com.example.diabetestracker.custom;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.diabetestracker.R;
import com.example.diabetestracker.model.Medication;

import java.util.ArrayList;

public class CustomMedicationList extends BaseAdapter {
    private Activity context;
    ArrayList<Medication> medEntries;
    public CustomMedicationList(Activity context,ArrayList medEntries)
    {
        this.context=context;
        this.medEntries=medEntries;
    }
    public static class ViewHolder
    {
        TextView date;
        TextView time;
        TextView med;
        TextView dos;
    }
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View row=convertView;
        LayoutInflater inflater=context.getLayoutInflater();
        CustomMedicationList.ViewHolder vh;
        if(convertView==null)
        {
            vh=new CustomMedicationList.ViewHolder();
            row=inflater.inflate(R.layout.med_row_item,null,true);
            vh.date=row.findViewById(R.id.date);
            vh.time=row.findViewById(R.id.time);
            vh.med=row.findViewById(R.id.med);
            vh.dos=row.findViewById(R.id.dose);
            row.setTag(vh);
        }
        else
        {
            vh=(CustomMedicationList.ViewHolder)convertView.getTag();
        }
        vh.date.setText(medEntries.get(position).getDate());
        vh.time.setText(medEntries.get(position).getTime());
        vh.med.setText(medEntries.get(position).getMedication());
        vh.dos.setText(String.valueOf(medEntries.get(position).getDosage())+medEntries.get(position).getUnit());
        return row;
    }
    @Override
    public int getCount() {
        if(medEntries.size()<=0)
        {
            return 1;
        }
        return medEntries.size();
    }

    @Override
    public Object getItem(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }
}

