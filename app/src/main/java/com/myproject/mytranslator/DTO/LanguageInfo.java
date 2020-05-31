package com.myproject.mytranslator.DTO;

import android.os.Parcel;
import android.os.Parcelable;

public class LanguageInfo implements Parcelable {

    private String textKorean;
    private String textEnglish;

    public LanguageInfo() {
    }

    public LanguageInfo(String textKorean, String textEnglish) {
        this.textKorean = textKorean;
        this.textEnglish = textEnglish;
    }

    protected LanguageInfo(Parcel in) {
        textKorean = in.readString();
        textEnglish = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(textKorean);
        dest.writeString(textEnglish);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<LanguageInfo> CREATOR = new Creator<LanguageInfo>() {
        @Override
        public LanguageInfo createFromParcel(Parcel in) {
            return new LanguageInfo(in);
        }

        @Override
        public LanguageInfo[] newArray(int size) {
            return new LanguageInfo[size];
        }
    };

    public String getTextKorean() {
        return textKorean;
    }

    public void setTextKorean(String textKorean) {
        this.textKorean = textKorean;
    }

    public String getTextEnglish() {
        return textEnglish;
    }

    public void setTextEnglish(String textEnglish) {
        this.textEnglish = textEnglish;
    }
}
