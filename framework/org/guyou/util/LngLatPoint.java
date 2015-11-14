package org.guyou.util;


public class LngLatPoint {
	
	public static final double	EARTH_RADIUS	= 6378.137;
	public static final double LNG_MAX = 180;
	public static final double LNG_MIN = -180;
	public static final double LAT_MAX = 90;
	public static final double LAT_MIN = -90;
	
	public double lng;
	public double lat;

	public LngLatPoint() {
	}

	public LngLatPoint(LngLatPoint p) {
		this.lng=p.lng;
		this.lat = p.lat;
	}

	public LngLatPoint(double lng, double lat) {
		this.lng = lng;
		this.lat = lat;
	}
	
	public LngLatPoint(String value) {
		String[] d = value.split(",");
		this.lng = Double.valueOf(d[0].trim());
		this.lat = Double.valueOf(d[1].trim());
	}
	
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		long temp;
		temp = Double.doubleToLongBits(lat);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		temp = Double.doubleToLongBits(lng);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		return result;
	}

	public boolean equals(Object obj) {
		if ( this == obj ) return true;
		if ( obj == null ) return false;
		if ( getClass() != obj.getClass() ) return false;
		LngLatPoint other = (LngLatPoint) obj;
		if ( Double.doubleToLongBits(lat) != Double.doubleToLongBits(other.lat) ) return false;
		if ( Double.doubleToLongBits(lng) != Double.doubleToLongBits(other.lng) ) return false;
		return true;
	}

	public String key() {
		return lng + "," + lat;
	}
	
	
	private static double rad(double d) {
		return d * Math.PI / 180.0;
	}

	/**
	 * 距离
	 * @param lng1
	 * @param lat1
	 * @param lng2
	 * @param lat2
	 * @return Km
	 */
	public static double getDistance( double lng1,double lat1, double lng2,double lat2) {
		double radLat1 = rad(lat1);
		double radLat2 = rad(lat2);
		double a = radLat1 - radLat2;
		double b = rad(lng1) - rad(lng2);
		double s = 2 * Math.asin(Math.sqrt(Math.pow(Math.sin(a / 2), 2) + Math.cos(radLat1) * Math.cos(radLat2) * Math.pow(Math.sin(b / 2), 2)));
		s = s * EARTH_RADIUS;
		return s;
	}
	
	public static double getDistance(LngLatPoint pos1,LngLatPoint pos2) {
		return getDistance(pos1.lng, pos1.lat, pos2.lng, pos2.lat);
	}
	
	
	/**
	 * 距离sql
	 * @param lng1
	 * @param lat1
	 * @param lng2
	 * @param lat2
	 * @return
	 */
	public static String getDistance_sql(String lng1,String lat1,String lng2,String lat2){
		//第一点经纬度：lng1 lat1
		//第二点经纬度：lng2 lat2
		String sql = EARTH_RADIUS+"*2*asin(sqrt(pow(sin((lat1*pi()/180-lat2*pi()/180)/2),2)+cos(lat1*pi()/180)*cos(lat2*pi()/180)*pow(sin( (lng1*pi()/180-lng2*pi()/180)/2),2)))";
		return sql.replace("lat1", lat1).replace("lng1", lng1).replace("lat2", lat2).replace("lng2", lng2);
	}
	
	/**
	 * 距离sql
	 * @param lng1
	 * @param lat1
	 * @param lng2
	 * @param lat2
	 * @return
	 */
	public static String getDistance_sql(LngLatPoint pos1,LngLatPoint pos2){
		return getDistance_sql(pos1.lng+"", pos1.lat+"", pos2.lng+"", pos2.lat+"");
	}
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		System.out.println(getDistance(33, 51, 34, 51));
		System.out.println(getDistance(116.33729771663752, 51, 116.43729771663752, 51));

	}
}
