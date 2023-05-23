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

    /**
     * Контекст
     */
    private Context context;

    /**
     * Названия конфигураций
     */
    private List<String> names;

    /**
     * Значения параметров конфигураций
     */
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

    /**
     * Переопределяет исходный метод получения View
     *
     * @param position The position of the item within the adapter's data set of the item whose view
     *        we want.
     * @param convertView The old view to reuse, if possible. Note: You should check that this view
     *        is non-null and of an appropriate type before using. If it is not possible to convert
     *        this view to display the correct data, this method can create a new view.
     *        Heterogeneous lists can specify their number of view types, so that this View is
     *        always of the right type (see {@link #getViewTypeCount()} and
     *        {@link #getItemViewType(int)}).
     * @param parent The parent that this view will eventually be attached to
     * @return View
     */
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
