package com.flower.customer.beans;

public class ReceiveXmlEntity {
	 
	  private String return_code ="";
	  private String return_msg ="";
	  private String appid="";
	  private String mch_id ="";
	  private String nonce_str ="";//随机字符串
	  private String sign="";//签名
	  private String result_code ="";//业务结果
	  private String prepay_id ="";
	  private String trade_type="";//交易类型
	  private String device_info="";//设备号
	  private String err_code ="";//错误代码
	  private String err_code_des ="";//错误描述
	  private String openid ="";//用户在商户appId 下的唯一标示
	  private String is_subscribe ="";//是否关注公众账号
	  private String bank_type ="";//付款银行
	  private String total_fee ="";//总金额
	  private String fee_type ="";//货币类型
	  private String cash_fee ="";//现金支付金额
	  private String cash_fee_type ="";//现金支付货币类型
	  private String coupon_fee ="";//代金券或立减优惠金额
	  private String coupon_count ="";//代金券或立减优惠使用数量
	  private String coupon_id_$n ="";//代金券或立减优惠ID
	  private String coupon_fee_$n ="";//单个代金券或立减优惠支付金额
	  private String transaction_id ="";//微信支付订单号
	  private String out_trade_no ="";//商户订单号
	  private String attach ="";//商家数据包
	  private String time_end ="";//支付完成时间
	
	  
	public String getReturn_code() {
		return return_code;
	}
	public void setreturn_code(String return_code) {
		this.return_code = return_code;
	}
	public String getReturn_msg() {
		return return_msg;
	}
	public void setreturn_msg(String return_msg) {
		this.return_msg = return_msg;
	}
	public String getAppid() {
		return appid;
	}
	public void setappid(String appid) {
		this.appid = appid;
	}
	public String getMch_id() {
		return mch_id;
	}
	public void setmch_id(String mch_id) {
		this.mch_id = mch_id;
	}
	public String getNonce_str() {
		return nonce_str;
	}
	public void setnonce_str(String nonce_str) {
		this.nonce_str = nonce_str;
	}
	public String getSign() {
		return sign;
	}
	public void setsign(String sign) {
		this.sign = sign;
	}
	public String getResult_code() {
		return result_code;
	}
	public void setresult_code(String result_code) {
		this.result_code = result_code;
	}
	public String getPrepay_id() {
		return prepay_id;
	}
	public void setprepay_id(String prepay_id) {
		this.prepay_id = prepay_id;
	}
	public String getTrade_type() {
		return trade_type;
	}
	public void settrade_type(String trade_type) {
		this.trade_type = trade_type;
	}
	public String getDevice_info() {
		return device_info;
	}
	public void setdevice_info(String device_info) {
		this.device_info = device_info;
	}
	public String getErr_code() {
		return err_code;
	}
	public void seterr_code(String err_code) {
		this.err_code = err_code;
	}
	public String getErr_code_des() {
		return err_code_des;
	}
	public void seterr_code_des(String err_code_des) {
		this.err_code_des = err_code_des;
	}
	public String getOpenid() {
		return openid;
	}
	public void setopenid(String openid) {
		this.openid = openid;
	}
	public String getIs_subscribe() {
		return is_subscribe;
	}
	public void setis_subscribe(String is_subscribe) {
		this.is_subscribe = is_subscribe;
	}
	public String getBank_type() {
		return bank_type;
	}
	public void setbank_type(String bank_type) {
		this.bank_type = bank_type;
	}
	public String getTotal_fee() {
		return total_fee;
	}
	public void settotal_fee(String total_fee) {
		this.total_fee = total_fee;
	}
	public String getFee_type() {
		return fee_type;
	}
	public void setfee_type(String fee_type) {
		this.fee_type = fee_type;
	}
	public String getCash_fee() {
		return cash_fee;
	}
	public void setcash_fee(String cash_fee) {
		this.cash_fee = cash_fee;
	}
	public String getCash_fee_type() {
		return cash_fee_type;
	}
	public void setcash_fee_type(String cash_fee_type) {
		this.cash_fee_type = cash_fee_type;
	}
	public String getCoupon_fee() {
		return coupon_fee;
	}
	public void setcoupon_fee(String coupon_fee) {
		this.coupon_fee = coupon_fee;
	}
	public String getCoupon_count() {
		return coupon_count;
	}
	public void setcoupon_count(String coupon_count) {
		this.coupon_count = coupon_count;
	}
	public String getCoupon_id_$n() {
		return coupon_id_$n;
	}
	public void setcoupon_id_$n(String coupon_id_$n) {
		this.coupon_id_$n = coupon_id_$n;
	}
	public String getCoupon_fee_$n() {
		return coupon_fee_$n;
	}
	public void setcoupon_fee_$n(String coupon_fee_$n) {
		this.coupon_fee_$n = coupon_fee_$n;
	}
	public String getTransaction_id() {
		return transaction_id;
	}
	public void settransaction_id(String transaction_id) {
		this.transaction_id = transaction_id;
	}
	public String getOut_trade_no() {
		return out_trade_no;
	}
	public void setout_trade_no(String out_trade_no) {
		this.out_trade_no = out_trade_no;
	}
	public String getAttach() {
		return attach;
	}
	public void setattach(String attach) {
		this.attach = attach;
	}
	public String getTime_end() {
		return time_end;
	}
	public void settime_end(String time_end) {
		this.time_end = time_end;
	}
}
