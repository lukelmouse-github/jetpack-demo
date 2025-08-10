package a;

/**
 * Generated code, Don't modify!!!
 * Created by kymjs, and APT Version is 1.2.2.
 * JDK Version is 11.0.24.
 */
@androidx.annotation.Keep
public class ServiceProvider__TheRouter__1188641689 implements com.therouter.inject.Interceptor {

	public static final String TAG = "Created by kymjs, and APT Version is 1.2.2.";
	public static final String THEROUTER_APT_VERSION = "1.2.2";
	public static final String FLOW_TASK_JSON = "{}";

	public <T> T interception(Class<T> clazz, Object... params) {
		T obj = null;
		if (com.example.core.common.service.LoginService.class.equals(clazz) && params.length == 0) {
			//type verification during compilation prevents the actual return type of the method from mismatching with the return type declared by the annotation
			com.example.core.common.service.LoginService returnType = new com.example.feature.login.service.LoginServiceImpl();
			obj = (T) returnType;
		} else {

        }
        return obj;
    }

	public static void addFlowTask(android.content.Context context, com.therouter.flow.Digraph digraph) {
	}
}
