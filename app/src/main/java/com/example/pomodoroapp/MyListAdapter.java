package com.example.pomodoroapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

/**
 * Кастомный адаптер для отображения информации о конфигурациях
 */
public class MyListAdapter extends ArrayAdapter<String> {

    private Context context;
    private List<String> names;
    private List<String> parameters;

    /**
     * Конструктор
     * @param context Контекст
     * @param names Названия конфигураций
     * @param parameters Параметры конфигураций
     */
    public MyListAdapter(Context context, List<String> names, List<String> parameters) {
        super(context, R.layout.name_item, names);
        this.context = context;
        this.names = names;
        this.parameters = parameters;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View rowView = convertView;
        if (rowView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            rowView = inflater.inflate(R.layout.name_item, parent, false);
        }
        TextView tvName = (TextView) rowView.findViewById(R.id.item_name);
        TextView tvParameters = (TextView) rowView.findViewById(R.id.item_parameters);

        tvName.setText(names.get(position));
        tvParameters.setText(parameters.get(position));

        return rowView;
    }
}
