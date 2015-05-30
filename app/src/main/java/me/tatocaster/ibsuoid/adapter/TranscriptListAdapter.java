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
            viewHolder.studyYear = (TextView) convertView.findViewById(R.id.study_year);
            viewHolder.semesterName = (TextView) convertView.findViewById(R.id.semester_name);
            viewHolder.moduleName = (TextView) convertView.findViewById(R.id.module_name);
            viewHolder.academicYear = (TextView) convertView.findViewById(R.id.academic_year);
            viewHolder.subjectName = (TextView) convertView.findViewById(R.id.subject_name);
            viewHolder.studentECTS = (TextView) convertView.findViewById(R.id.student_ects);
            viewHolder.lectureHours = (TextView) convertView.findViewById(R.id.lecture_hours);
            viewHolder.pointMid = (TextView) convertView.findViewById(R.id.point_mid);
            viewHolder.pointFinal = (TextView) convertView.findViewById(R.id.point_final);
            viewHolder.pointXFinal = (TextView) convertView.findViewById(R.id.point_x_final);
            viewHolder.pointMakeup = (TextView) convertView.findViewById(R.id.point_makeup);
            viewHolder.grossGrade = (TextView) convertView.findViewById(R.id.gross_grade);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (viewHolder) convertView.getTag();
        }

        Transcript transcriptItem = transcriptList.get(position);

        if (transcriptItem != null) {
            viewHolder.studyYear.setText("Year: " + transcriptItem.getStudyYearName());
            viewHolder.semesterName.setText("Semester: " + transcriptItem.getSemesterName());
            viewHolder.moduleName.setText("Module Name: " + transcriptItem.getModuleName());
            viewHolder.academicYear.setText("Academic Year: " + transcriptItem.getAcademicYear());
            viewHolder.subjectName.setText("Subject Name: " + transcriptItem.getSubjectName());
            viewHolder.studentECTS.setText("ECTS: " + transcriptItem.getStudentECTS());
            viewHolder.lectureHours.setText("Hour: " + transcriptItem.getLectureHours());
            viewHolder.pointMid.setText("Mid: " + transcriptItem.getPointMid());
            viewHolder.pointFinal.setText("Final: " + transcriptItem.getPointFinal());
            viewHolder.pointXFinal.setText("Excuse final: " + transcriptItem.getPointXFinal());
            viewHolder.pointMakeup.setText("Makeup: " + transcriptItem.getPointMakeUp());
            viewHolder.grossGrade.setText("Grade: " + transcriptItem.getStudentGrade());
        }

        return convertView;
    }

    static class viewHolder {
        TextView studyYear,
                semesterName,
                moduleName,
                academicYear,
                subjectName,
                studentECTS,
                lectureHours,
                pointMid,
                pointFinal,
                pointXFinal,
                pointMakeup,
                grossGrade;
    }

}