package com.example.android.memoapp;

public class Notemodel
{
public String noteTitle,noteTime;

    public Notemodel() {
    }

    public Notemodel(String noteTitle, String noteTime) {
        this.noteTitle = noteTitle;
        this.noteTime = noteTime;
    }

    public String getNoteTitle() {
        return noteTitle;
    }

    public String getNoteTime() {
        return noteTime;
    }

    public void setNoteTitle(String noteTitle) {
        this.noteTitle = noteTitle;
    }

    public void setNoteTime(String noteTime) {
        this.noteTime = noteTime;
    }
}
