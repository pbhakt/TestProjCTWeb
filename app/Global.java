import static com.clicktable.util.Constants.ACCESS_TOKEN;
import static com.clicktable.util.ResponseCodes.ACCESS_TOKEN_MISSING;
import static com.clicktable.util.ResponseCodes.BAD_REQUEST;
import static com.clicktable.util.ResponseCodes.INTERNAL_SERVER_ERROR;
import static com.clicktable.util.ResponseCodes.INVALID_ACCESS_TOKEN;
import static com.clicktable.util.ResponseCodes.ROUTE_NOT_FOUND;
import static com.clicktable.util.ResponseCodes.UNAUTHORIZED;
import static play.mvc.Results.badRequest;
import static play.mvc.Results.internalServerError;
import static play.mvc.Results.notFound;

import java.util.ArrayList;
import java.util.List;

import org.jboss.logging.MDC;
//import org.slf4j.MDC;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import play.Application;
import play.GlobalSettings;
import play.Logger;
import play.api.libs.Crypto;
import play.api.mvc.EssentialFilter;
import play.i18n.Messages;
import play.libs.F.Promise;
import play.libs.Json;
import play.libs.Scala;
import play.mvc.Action;
import play.mvc.Http;
import play.mvc.Http.Cookie;
import play.mvc.Http.Request;
import play.mvc.Http.RequestHeader;
import play.mvc.Result;
import scala.Tuple2;
import scala.collection.Seq;

import com.clicktable.config.Neo4jConfig;
import com.clicktable.filter.LoggingFilter;
import com.clicktable.model.Permission;
import com.clicktable.model.Role;
import com.clicktable.model.UserInfoModel;
import com.clicktable.response.BaseResponse;
import com.clicktable.response.ErrorResponse;
import com.clicktable.service.intf.AuthorizationService;
import com.clicktable.service.intf.ThreadSchedulerService;
import com.clicktable.util.Constants;
import com.clicktable.util.ErrorCodes;
import com.clicktable.util.UtilityMethods;
import com.clicktable.validate.ValidationError;

public class Global extends GlobalSettings {

	private ApplicationContext ctx;

	private String requestBody;

	@Override
	public <T extends EssentialFilter> Class<T>[] filters() {
		return new Class[] { LoggingFilter.class };
	}

	private class ActionWrapper extends Action.Simple {
		public ActionWrapper(Action<?> action) {
			this.delegate = action;
		}

		@Override
		public Promise<Result> call(Http.Context ctx) throws java.lang.Throwable {
			Promise<Result> result = this.delegate.call(ctx);
			Http.Response response = ctx.response();
		   // response.setHeader("Access-Control-Allow-Origin","*");
			response.setHeader("Access-Control-Allow-Origin", (null == ctx.request().getHeader("Origin")) ? "*" : ctx.request().getHeader("Origin").toString());
			response.setHeader("Access-Control-Allow-Credentials", "true");
			return result;

		}
	}

	private class ForbiddenAction extends Action.Simple {
		String msg;
		BaseResponse response;

		public ForbiddenAction(Action<?> action, String forbiddenMessage) {
			this.msg = forbiddenMessage;
			this.delegate = action;
		}

		public ForbiddenAction(Action<?> action, BaseResponse forbiddenMessage) {
			this.response = forbiddenMessage;
			this.delegate = action;
		}

		@Override
		public Promise<Result> call(Http.Context ctx) throws java.lang.Throwable {

			Http.Response res = ctx.response();
			 //res.setHeader("Access-Control-Allow-Origin","*");
			res.setHeader("Access-Control-Allow-Origin", (null == ctx.request().getHeader("Origin")) ? "*" : ctx.request().getHeader("Origin").toString());
			res.setHeader("Access-Control-Allow-Credentials", "true");
			if (null == response) {
				BaseResponse response = new BaseResponse();
				response.createResponse(msg, false);
				Http.Response httpresponse = ctx.response();

				// httpresponse.setHeader("Access-Control-Allow-Origin","*");
				httpresponse.setHeader("Access-Control-Allow-Origin", (null == ctx.request().getHeader("Origin")) ? "*" : ctx.request().getHeader("Origin").toString());
				httpresponse.setHeader("Access-Control-Allow-Credentials", "true");
				if(msg.equals(ACCESS_TOKEN_MISSING))
				{
					return Promise.<Result> pure(forbidden(Json.toJson(response)));
				}
				else
				{
					return Promise.<Result> pure(unauthorized(Json.toJson(response)));
				}

			} else {
				response.createResponse(null, false);

				Http.Response httpresponse = ctx.response();
				 //httpresponse.setHeader("Access-Control-Allow-Origin","*");
				httpresponse.setHeader("Access-Control-Allow-Origin", (null == ctx.request().getHeader("Origin")) ? "*" : ctx.request().getHeader("Origin").toString());
				httpresponse.setHeader("Access-Control-Allow-Credentials", "true");
				return Promise.<Result> pure(unauthorized(Json.toJson(response)));
			}

		}
	}

	@Override
	public Action<?> onRequest(Http.Request request, java.lang.reflect.Method actionMethod) {
		
		if (request.method().equals("OPTIONS")) {
			return new ActionWrapper(super.onRequest(request, actionMethod));


		} else {/*
			
			 * Start Validating OAUTH Token



		// make changes to if condition - take into account api url and method  - Reference permission.json
			List<Role> bypassReq = ctx.getBean(Permission.class).getRoles(request.path(), request.method());
			if (!bypassReq.isEmpty()){
				if ( null != request.getHeader("Authorization")) {		
					System.out.println(" Request----"+request.path());
					try{

						String header = request.getHeader("Authorization");
						Iterator<Cookie> i = request.cookies().iterator();
						int i1 = 0;
						String userToken = null;
						while (i.hasNext()) {
							i1++;
							Cookie cookie = i.next();

							if (null != cookie && cookie.name().equalsIgnoreCase("UserName")) {
								userToken = cookie.value();
								if (null == play.cache.Cache.get(userToken)) {
									BaseResponse response = new BaseResponse();
									response.setResponseCode(ErrorCodes.INVALID_ACCESS_TOKEN);
									response.setResponseMessage(Constants.INVALID_AUTH_ACCESS_TOKEN);
									return new ForbiddenAction(super.onRequest(request, actionMethod), response);
								} else {
									Map<String, String> map = (Map<String, String>) play.cache.Cache.get(userToken);
									System.out.println(" +++++++++MAP SIZE+++++++++" + map.size());
									Logger.info(" +++++++++MAP SIZE+++++++++" + map.size());
									String authorizationToken = "";
									String ttl = "";
									Iterator itr = map.keySet().iterator();

									while (itr.hasNext()) {
										String key = itr.next().toString();
										if (key.equalsIgnoreCase("Authorization")) {
											authorizationToken = map.get(key);
											authorizationToken = authorizationToken.replace("Bearer", "");
										}
										if (key.equalsIgnoreCase("TTL")) {
											ttl = map.get(key).toString();
										}

									}
									System.out.println("---- Bearer:" + header.replace("Bearer", ""));
									if (!"".equalsIgnoreCase(authorizationToken) && authorizationToken.equalsIgnoreCase(header.replace("Bearer", "").trim())) {
										System.out.println("Calender Diff !!!!" + (Long.valueOf(ttl) - Calendar.getInstance().getTimeInMillis()));

										if (Calendar.getInstance().getTimeInMillis() > Long.valueOf(ttl)) {
											BaseResponse response = new BaseResponse();
											response.setResponseCode(ErrorCodes.INVALID_ACCESS_EXPIRED);
											response.setResponseMessage(Constants.INVALID_AUTH_ACCESS_TOKEN_EXPIRED);
											List<ValidationError> errorList = new ArrayList<ValidationError>();

											errorList.add(new ValidationError(null, Json.toJson(response)));
											return new ForbiddenAction(super.onRequest(request, actionMethod), new ErrorResponse(ErrorCodes.INVALID_ACCESS_EXPIRED, errorList));
										}
									} else {
										BaseResponse response = new BaseResponse();
										response.setResponseCode(ErrorCodes.INVALID_ACCESS_TOKEN);
										response.setResponseMessage(Constants.INVALID_AUTH_ACCESS_TOKEN);
										return new ForbiddenAction(super.onRequest(request, actionMethod), response);
									}

								}
							}

						}
						if (i1 == 0) {
							BaseResponse response = new BaseResponse();
							response.setResponseCode(ErrorCodes.AUTH_TOKEN_MISSING);
							response.setResponseMessage(Constants.AUTH_ACCESS_TOKEN_MISSING);
							return new ForbiddenAction(super.onRequest(request, actionMethod), response);
						}

					} catch (Exception e) {
						e.printStackTrace();
						Logger.error(e.getMessage());
					}

				} else {
					// TODO : Comment Code when testing locally with POSTMAN
					BaseResponse response = new BaseResponse();
					response.setResponseCode(ErrorCodes.INVALID_AUTH_TOKEN);
					response.setResponseMessage(Constants.AUTH_ACCESS_TOKEN_MISSING);
					return new ForbiddenAction(super.onRequest(request, actionMethod), response);
				}
			} else {
				System.out.println("***************** ByPass Request*****************" + request.path());
			}

			
			 * End Validating OAUTH Token */
			 


			if (request.method().equals("POST") || request.method().equals("PUT")) {
				if ((request.getHeader("Content-Type") != null) && request.getHeader("Content-Type").equals("application/json")) {
					requestBody = Json.stringify(request.body().asJson());
					UtilityMethods.requestTracking(request.method(), request.remoteAddress(), request.host() + request.path(), requestBody);
					Logger.debug(Json.stringify(request.body().asJson()));
				}
			} else {
				requestBody = Json.stringify(Json.toJson(UtilityMethods.convertQueryStringToMap(request.queryString())));
				UtilityMethods.requestTracking(request.method(), request.remoteAddress(), request.host() + request.path(), requestBody);

			}

			MDC.put("Device", request.remoteAddress());
			MDC.put("Url", request.uri());
			MDC.put("RequestPayload", requestBody);
			List<Role> roles = ctx.getBean(Permission.class).getRoles(request.path(), request.method());
			AuthorizationService authService = ctx.getBean(AuthorizationService.class);
			
		/*	Cookie tokenCookie = request.cookies().get(ACCESS_TOKEN);
			String tokenStr = tokenCookie.value();
			String tokenFromCookie = Crypto.decryptAES(tokenStr);*/
			
			String token = request.getHeader(ACCESS_TOKEN);
			String forbiddenMessage = INVALID_ACCESS_TOKEN;
			if (token == null) {
				forbiddenMessage = ACCESS_TOKEN_MISSING;
			}
			
			
			Logger.debug("token is " + token);
			/*System.out.println("token is " + token);*/
			// get logged in user info by using token from logged in user map
			Long roleId = 0L;
			UserInfoModel userInfo = authService.getUserInfoByToken(token);
			System.out.println("user info is " + userInfo);

			if (userInfo != null) {
				roleId = userInfo.getRoleId();
				MDC.put("User", userInfo.getEmail());
			}
			if (roleId > 0 && !authService.hasAccess(roleId, roles))
				forbiddenMessage = UNAUTHORIZED;
			if (!roles.isEmpty() && !authService.hasAccess(roleId, roles)) {
				return new ForbiddenAction(super.onRequest(request, actionMethod), forbiddenMessage);
			}

			Logger.debug("super on request calling");

		}

		return new ActionWrapper(super.onRequest(request, actionMethod));
	}

	private String validateHeaderValue(Request request, String headerValue, String expectedValue) {
		String requestHeader = request.getHeader(headerValue);
		String message = null;
		if (requestHeader == null)
			message = Messages.get(ErrorCodes.REQUIRED, headerValue);
		else if (!requestHeader.equals(expectedValue)) {
			message = Messages.get(ErrorCodes.INVALID_HEADER, headerValue);
		}
		return message;
	}

	private static class CORSResult implements Result {
		final private play.api.mvc.Result wrappedResult;
		Http.Request req = Http.Context.current().request();

		// String origin=null;

		public CORSResult(play.mvc.Results.Status status) {
			List<Tuple2<String, String>> list = new ArrayList<Tuple2<String, String>>();
			// System.out.println("-----------------HEADER========="+(null==req.getHeader("Origin"))?"*":req.getHeader("Origin").toString());
			// Tuple2<String, String> t = new Tuple2<String,
			// String>("Access-Control-Allow-Origin", "*");

			Tuple2<String, String> t = new Tuple2<String, String>("Access-Control-Allow-Origin", (null==req.getHeader("Origin"))?"*":req.getHeader("Origin").toString());
			Tuple2<String, String> t1 = new Tuple2<String, String>("Access-Control-Allow-Credentials", "true");

			list.add(t);
			 list.add(t1);
			Seq<Tuple2<String, String>> seq = Scala.toSeq(list);
			wrappedResult = status.toScala().withHeaders(seq);
		}

		public play.api.mvc.Result toScala() {
			return this.wrappedResult;
		}
	}

	public Promise<Result> onHandlerNotFound(RequestHeader request) {
		BaseResponse response = new BaseResponse();
		response.createResponse(ROUTE_NOT_FOUND, false);
		return Promise.<Result> pure(new CORSResult(notFound(Json.toJson(response))));
	}

	public Promise<Result> onError(RequestHeader request, Throwable t) {
		List<ValidationError> errorList = new ArrayList<ValidationError>();
		errorList.add(new ValidationError(null, Json.toJson(t).get("cause").get("localizedMessage")));
		BaseResponse response = new ErrorResponse(INTERNAL_SERVER_ERROR, errorList);
		return Promise.<Result> pure(new CORSResult(internalServerError(Json.toJson(response))));
		// return
		// Promise.<Result>pure(internalServerError(Json.toJson(response)));
	}

	public Promise<Result> onBadRequest(RequestHeader request, String error) {
		BaseResponse response = new BaseResponse();
		response.setResponseCode(BAD_REQUEST);
		response.setResponseMessage(error);
		response.setResponseStatus(false);
		return Promise.<Result> pure(new CORSResult(badRequest(Json.toJson(response))));
	}

	@Override
	public void onStart(Application app) {
		ctx = new AnnotationConfigApplicationContext(Neo4jConfig.class);
		ThreadSchedulerService scheduler = ctx.getBean(ThreadSchedulerService.class);
		scheduler.startThreads();
	}

	@Override
	public <A> A getControllerInstance(Class<A> clazz) {
		A result;
		result = ctx.getBean(clazz);
		return result;
	}

	@Override
	public void onStop(Application app) {
		if (ctx != null)
			((AnnotationConfigApplicationContext) ctx).close();
	}

}
