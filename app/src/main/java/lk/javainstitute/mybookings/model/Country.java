package lk.javainstitute.mybookings.model;


import android.os.Parcel;
import android.os.Parcelable;
public class Country implements Parcelable {
    private String name;
    private String code;
    private String phoneCode;
    private String emoji;
    public Country(String name, String code, String phoneCode, String emoji) {
        this.name = name;
        this.code = code;
        this.phoneCode = phoneCode;
        this.emoji = emoji;
    }
    public String getName() {
        return name;
    }
    public String getCode() {
        return code;
    }
    public String getPhoneCode() {
        return phoneCode;
    }
    public String getEmoji() {
        return emoji;
    }
    // Implement Parcelable methods to allow passing Country objects between activities
    protected Country(Parcel in) {
        name = in.readString();
        code = in.readString();
        phoneCode = in.readString();
        emoji = in.readString();
    }
    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(name);
        dest.writeString(code);
        dest.writeString(phoneCode);
        dest.writeString(emoji);
    }
    @Override
    public int describeContents() {
        return 0;
    }
    public static final Creator<Country> CREATOR = new Creator<Country>() {
        @Override
        public Country createFromParcel(Parcel in) {
            return new Country(in);
        }
        @Override
        public Country[] newArray(int size) {
            return new Country[size];
        }
    };
}
