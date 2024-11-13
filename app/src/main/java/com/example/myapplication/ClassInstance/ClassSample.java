package com.example.myapplication.ClassInstance;

public class ClassSample {
    private int id;
    private String classType;
    private String teacher;
    private String dayOfWeek;

    public ClassSample(){

    }
    public ClassSample(int id, String classType, String teacher, String dayOfWeek) {
        this.id = id;
        this.classType = classType;
        this.teacher = teacher;
        this.dayOfWeek = dayOfWeek;
    }

    public int getId(){
        return this.id;
    }

    public String getTeacherName(){
        return teacher;
    }

    public String getClassType(){
        return classType;
    }

    public String getDayOfWeek(){return dayOfWeek;}

    public void setId(int id){
        this.id = id;
    }

    public void setClassType(String classType){
        this.classType = classType;
    }
    public void setTeacher(String teacher){
        this.teacher = teacher;
    }

    public void setDayOfWeek(String dayOfWeek){
        this.dayOfWeek = dayOfWeek;
    }
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        ClassSample that = (ClassSample) obj;
        return id == that.id;
    }

    @Override
    public int hashCode() {
        return id;
    }
}
