package com.tradegene.risk_management.commandservice.domain.code;

public class Code {

	// 여부
	public static final String YES = "Y";
    public static final String NO = "N";

    // 매도매수구분코드
    public static final String SELL_BUY_TYPE_CODE_SELL = "1"; // 매도
    public static final String SELL_BUY_TYPE_CODE_BUY  = "2"; // 매수 
    
    // 금액유형구분코드
    public static final String AMOUNT_PATTERN_TYPE_CODE_TAX = "01"; // 세금
    public static final String AMOUNT_PATTERN_TYPE_CODE_FEE = "02"; // 수수료
    
    // 포지션유형코드
    public static final String POSITION_TYPE_CODE_PRINCIPAL = "01"; // 원금

    public static final String KRW = "KRW"; // 원화

    // 상태코드
    public static final String NOT_STARTED = "01";
    public static final String IN_PROGRESS = "02";
    public static final String COMPLETED = "03";
    public static final String FAILED = "04";
}
