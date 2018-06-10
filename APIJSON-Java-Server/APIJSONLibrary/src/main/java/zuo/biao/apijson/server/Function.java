/*Copyright ©2016 TommyLemon(https://github.com/TommyLemon/APIJSON)

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.*/

package zuo.biao.apijson.server;

import com.alibaba.fastjson.JSONObject;

import zuo.biao.apijson.Log;
import zuo.biao.apijson.StringUtil;

/**可远程调用的函数类
 * @author Lemon
 */
public class Function {
	private static final String TAG = "Function";

	/**反射调用
	 * @param fun
	 * @param request
	 * @param function 例如get(Map:map,key)，参数只允许引用，不能直接传值
	 * @return
	 */
	public static Object invoke(@NotNull Function fun, @NotNull JSONObject request, @NotNull String function) throws Exception {

		int start = function.indexOf("(");
		int end = function.lastIndexOf(")");
		String method = function.substring(0, start);
		if (StringUtil.isEmpty(method, true)) {
			Log.i(TAG, "invoke  StringUtil.isEmpty(method, true) >> return null;");
			return null;
		}

		String[] keys = StringUtil.split(function.substring(start + 1, end));

		int length = keys == null ? 0 : keys.length;

		Class<?>[] types = new Class<?>[length + 1];
		types[0] = JSONObject.class;

		Object[] values = new Object[length + 1];
		values[0] = request;

		for (int i = 0; i < length; i++) {
			types[i + 1] = String.class;
			values[i + 1] = keys[i];
		}

		//碰到null就挂了！！！Number还得各种转换不灵活！不如直接传request和对应的key到函数里，函数内实现时自己 getLongValue,getJSONObject ...
		//			for (int i = 0; i < length; i++) {
		//				v = values[i] = request == null ? null : request.get(keys[i]);
		//				if (v instanceof Boolean) {
		//					types[i] = Boolean.class; //只支持JSON的几种类型 
		//				}
		//				else if (v instanceof Number) {
		//					types[i] = Number.class;
		//				}
		//				else if (v instanceof String) {
		//					types[i] = String.class;
		//				}
		//				else if (v instanceof JSONObject) { // Map) {
		//					types[i] = JSONObject.class;
		//					//性能比较差	values[i] = request.getJSONObject(keys[i]);
		//				}
		//				else if (v instanceof JSONArray) { // Collection) {
		//					types[i] = JSONArray.class;
		//					//性能比较差	values[i] = request.getJSONArray(keys[i]);
		//				}
		//				else { //FIXME 碰到null就挂了！！！
		//					throw new UnsupportedDataTypeException(keys[i] + ":value 中value不合法！远程函数 key():" + function + " 中的arg对应的值类型"
		//							+ "只能是 [Boolean, Number, String, JSONObject, JSONArray] 中的一种！");
		//				}
		//			}

		return invoke(fun, method, types, values); 
	}
	/**反射调用
	 * @param methodName
	 * @param parameterTypes
	 * @param args
	 * @return
	 */
	public static Object invoke(@NotNull Function fun, @NotNull String methodName, @NotNull Class<?>[] parameterTypes, @NotNull Object[] args) throws Exception {
		return fun.getClass().getDeclaredMethod(methodName, parameterTypes).invoke(fun, args);
	}

}