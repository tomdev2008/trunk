package com.example.map;

import android.os.Parcel;
import android.os.Parcelable;

public class LocationEntity implements Parcelable{
	private double radius;
	private int locType;
	private int satellite;//卫星
	private int latitude ;//纬度
	private int longitude ;//经度
	private String city;//城市
	private String district;//区县
	private String province;//省
	private String poi;//兴趣点
	private String address; 
	private String street; 
	private String street_number;
	public LocationEntity() {
	}

	public LocationEntity(Parcel out) {
		city=out.readString();
		address = out.readString();
		radius=out.readDouble();
		latitude = out.readInt();
		longitude = out.readInt();
	}
	
	public double getRadius() {
		return radius;
	}

	public void setRadius(double radius) {
		this.radius = radius;
	}

	public int getLocType() {
		return locType;
	}

	public void setLocType(int locType) {
		this.locType = locType;
	}

	public int getSatellite() {
		return satellite;
	}

	public void setSatellite(int satellite) {
		this.satellite = satellite;
	}

	public int getLatitude() {
		return latitude;
	}

	public void setLatitude(int latitude) {
		this.latitude = latitude;
	}

	public int getLongitude() {
		return longitude;
	}

	public void setLongitude(int longitude) {
		this.longitude = longitude;
	}

	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}

	public String getDistrict() {
		return district;
	}

	public void setDistrict(String district) {
		this.district = district;
	}

	public String getProvince() {
		return province;
	}

	public void setProvince(String province) {
		this.province = province;
	}

	public String getPoi() {
		return poi;
	}

	public void setPoi(String poi) {
		this.poi = poi;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getStreet() {
		return street;
	}

	public void setStreet(String street) {
		this.street = street;
	}

	public String getStreet_number() {
		return street_number;
	}

	public void setStreet_number(String street_number) {
		this.street_number = street_number;
	}

	public static Parcelable.Creator<LocationEntity> getCreator() {
		return CREATOR;
	}

	@Override
	public int describeContents() {
		return 0;
	}
	@Override
	public void writeToParcel(Parcel parcel, int i) {
		parcel.writeString(city);
		parcel.writeString(address);
		parcel.writeDouble(radius);
		parcel.writeInt(latitude);
		parcel.writeInt(longitude);
	}
	
	public static final Parcelable.Creator<LocationEntity> CREATOR = new Parcelable.Creator<LocationEntity>() {
		public LocationEntity createFromParcel(Parcel in) {
			return new LocationEntity(in);
		}

		public LocationEntity[] newArray(int size) {
			return new LocationEntity[size];
		}
	};
}
