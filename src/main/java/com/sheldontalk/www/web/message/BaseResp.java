/**
 * 
 */
package com.sheldontalk.www.web.message;

/**
 * @author Sheldon
 * @Date 2014年4月23日
 * @Time 上午9:56:10
 */
public class BaseResp {

	public Integer code;
	public String desc;

    @Override
    public String toString() {
        return "BaseResp{" +
                "code=" + code +
                ", desc='" + desc + '\'' +
                '}';
    }
}
