package me.tatocaster.ibsuoid.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

import me.tatocaster.ibsuoid.R;
import me.tatocaster.ibsuoid.model.Transcript;

/**
 * Created by tatocaster on 2015-05-05.
 */
public class TranscriptListAdapter extends BaseAdapter {

    private List<Transcript> transcriptList;
    private Context context;
    LayoutInflater inflater;

    @Override
    public int getCount() {
        return transcriptList.size();
    }

    public TranscriptListAdapter(List<Transcript> transcriptList, Context context) {
        this.context = context;
        this.transcriptList = transcriptList;
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        viewHolder viewHolder;

        if (convertView == null) {
            Context context = parent.getContext();
            inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.list_item, parent, false);
            viewHolder = new viewHolder();
            viewHolder.transcriptListItem = (TextView) convertView.findViewById(R.id.title_text);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (viewHolder) convertView.getTag();
        }

        Transcript transcriptItem = transcriptList.get(position);

        if (transcriptItem != null) {

            String strItem = "";

            strItem += "Year: " + transcriptItem.getStudyYearName() + "\n";
            strItem += "Semester: " + transcriptItem.getSemesterName() + "\n";
            strItem += "Module Name: " + transcriptItem.getModuleName() + "\n";
            strItem += "Academic Year: " + transcriptItem.getAcademicYear() + "\n";
            strItem += "Subject Name: " + transcriptItem.getSubjectName() + "\n";
            strItem += "ECTS: " + transcriptItem.getStudentECTS() + "\n";
            strItem += "Hour: " + transcriptItem.getLectureHours() + "\n";
            strItem += "Mid: " + transcriptItem.getPointMid() + "\n";
            strItem += "Final: " + transcriptItem.getPointFinal() + "\n";
            strItem += "Excuse final: " + transcriptItem.getPointXFinal() + "\n";
            strItem += "Makeup: " + transcriptItem.getPointMakeUp() + "\n";
            strItem += "Grade: " + transcriptItem.getStudentGrade();

            viewHolder.transcriptListItem.setText(strItem);

        }

        return convertView;
    }

    static class viewHolder {
        TextView transcriptListItem;
    }

}