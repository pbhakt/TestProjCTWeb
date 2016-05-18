package com.clicktable.filter;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import play.Logger;
import play.api.libs.iteratee.Execution;
import play.api.libs.iteratee.Iteratee;
import play.api.mvc.EssentialAction;
import play.api.mvc.EssentialFilter;
import play.api.mvc.RequestHeader;
import play.api.mvc.Result;
import play.libs.Scala;
import scala.Tuple2;
import scala.collection.Seq;
import scala.runtime.AbstractFunction1;
//import play.api.mvc.BodyParsers.parse;

import com.clicktable.service.intf.AuthorizationService;

public class LoggingFilter implements EssentialFilter {

	@Autowired
	AuthorizationService authService;

	public EssentialAction apply(final EssentialAction next) {

		return new TimeLoggingAction() {

			@Override
			public EssentialAction apply() {
				return next.apply();
			}

			@Override
			public Iteratee<byte[], Result> apply(final RequestHeader rh) {
				final long startTime = System.currentTimeMillis();

				Logger.info("Started " + rh.uri() + " from " + rh.remoteAddress() + " at " + startTime);
				return next.apply(rh).map(new AbstractFunction1<Result, Result>() {

					@Override
					public Result apply(Result v1) {
						long time = logTime(rh, startTime);
						List<Tuple2<String, String>> list = new ArrayList<Tuple2<String, String>>();
						Tuple2<String, String> t = new Tuple2<String, String>("Request-Time", String.valueOf(time));
						list.add(t);
						Seq<Tuple2<String, String>> seq = Scala.toSeq(list);
						return v1.withHeaders(seq);
					}
				}, Execution.defaultExecutionContext());
			}

			private long logTime(RequestHeader request, long startTime) {
				long endTime = System.currentTimeMillis();
				long requestTime = endTime - startTime;
				Logger.info("Completed " + request.uri() + " from " + request.remoteAddress() + " at " + endTime + " took " + requestTime);

				return requestTime;
			}
		};
	}

	public abstract class TimeLoggingAction extends AbstractFunction1<RequestHeader, Iteratee<byte[], Result>> implements EssentialAction {
	}

}
