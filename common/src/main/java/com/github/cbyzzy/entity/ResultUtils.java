package com.github.cbyzzy.entity;

/**
 * 消息结果工具类
 * ResultUtils用法：<br>
 * &nbsp;&nbsp; 正常返回值：<br>
 * &nbsp;&nbsp;&nbsp;&nbsp; ResultUtils.ok(): 返回默认信息"success" <br>
 * &nbsp;&nbsp;&nbsp;&nbsp; ResultUtils.ok(String): 返回字符串信息 <br>
 * &nbsp;&nbsp;&nbsp;&nbsp; ResultUtils.ok(Object): 返回对象 <br>
 * &nbsp;&nbsp;&nbsp;&nbsp; ResultUtils.page(List<?>): 返回分页对象，参数必须是分页集合 <br>
 * &nbsp;&nbsp; 异常返回值： <br>
 * &nbsp;&nbsp;&nbsp;&nbsp; ResultUtils.error(String): 返回错误描述 <br>
 * &nbsp;&nbsp;&nbsp;&nbsp; ResultUtils.error(Object): 返回错误对象 <br>
 * &nbsp;&nbsp;&nbsp;&nbsp; ResultUtils.error(Object, String) 返回错误对象及描述 <br>
 * </p>
 *
 */
public class ResultUtils {

	/**
	 * 构建执行正确的消息返回结果-默认消息
	 */
	public static ResultEntity<String> ok() {
		return ok("success");
	}

	/**
	 * 构建执行正确的消息返回结果
	 *
	 * @param msg
	 * @return
	 */
	public static ResultEntity<String> ok(String msg) {
		ResultEntity<String> vo = new ResultEntity<>();
		vo.setCode(200);
		vo.setData(msg);
		return vo;
	}

	/**
	 * 构建执行正确的数据返回结果
	 * 
	 * @param data
	 * @return
	 */
	public static <T> ResultEntity<T> ok(T data) {
		ResultEntity<T> vo = new ResultEntity<>();
		vo.setCode(200);
		vo.setData(data);
		return vo;
	}

	/**
	 * 构建执行异常的数据返回结果
	 * 
	 * @param data
	 * @return
	 */
	public static ResultEntity error(Object data) {
		ResultEntity vo = new ResultEntity();
		vo.setCode(500);
		vo.setData(data);
		return vo;
	}

	/**
	 * 构建执行异常的消息返回结果
	 *
	 * @param msg
	 * @return
	 */
	public static ResultEntity error(String msg) {
		ResultEntity vo = new ResultEntity();
		vo.setCode(500);
		vo.setMessage(msg);
		return vo;
	}

	/**
	 * 构建执行异常的消息返回结果
	 * 
	 * @param msg
	 * @return
	 */
	public static ResultEntity error(Object data, String msg) {
		ResultEntity vo = new ResultEntity();
		vo.setCode(500);
		vo.setData(data);
		vo.setMessage(msg);
		return vo;
	}

}
