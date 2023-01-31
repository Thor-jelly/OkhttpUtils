package com.jelly.thor.example.utils

import android.view.View
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import autodispose2.*
import autodispose2.androidx.lifecycle.AndroidLifecycleScopeProvider
import com.jakewharton.rxbinding4.view.clicks
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Maybe
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.core.ObservableTransformer
import io.reactivex.rxjava3.core.Scheduler
import io.reactivex.rxjava3.schedulers.Schedulers
import java.util.concurrent.TimeUnit

/**
 * 类描述：compose工具,暂时用这个compose方式 以后用kotlin后切换扩展方法重构一些方法 <br></br>
 * 创建人：臭菜 吴冬冬<br></br>
 * 创建时间：2022/4/18 11:31 <br></br>
 */
object RxJavaCompose {
    /**
     * io 转 main
     */
    @JvmStatic
    fun <T : Any> io2Main() = ObservableTransformer<T, T> { upstream ->
        upstream.subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
    }

    /**
     * 其他 转 main
     */
    @JvmStatic
    fun <T : Any> other2Main() = ObservableTransformer<T, T> { upstream ->
        upstream.observeOn(AndroidSchedulers.mainThread())
    }

    /**
     * 防止多点，默认切换到主线程
     */
    @JvmStatic
    @JvmOverloads
    fun <T : Any> preventMultipoint(scheduler: Scheduler = AndroidSchedulers.mainThread()) =
        ObservableTransformer<T, T> { upstream ->
            upstream.throttleFirst(2, TimeUnit.SECONDS)
                .observeOn(scheduler)
        }

    /**
     * 绑定生命周期，防止内存泄漏
     * 使用方法：放入RxJava Observable的to方法中
     */
    @JvmStatic
    @JvmOverloads
    fun <T : Any> getAutoDispose(
        owner: LifecycleOwner,
        event: Lifecycle.Event? = Lifecycle.Event.ON_DESTROY
    ): AutoDisposeConverter<T> {
        return if (event == null) {
            AutoDispose.autoDisposable(AndroidLifecycleScopeProvider.from(owner))
        } else {
            AutoDispose.autoDisposable(AndroidLifecycleScopeProvider.from(owner, event))
        }
    }
}

/////////////////////RxjavaE扩展方法
/**
 * 不防止多点，以后主要用来埋点
 */
fun View.preventMultipointNo(): Observable<Unit> {
    return this.clicks()
}

/**
 * 不防止多点，以后主要用来埋点
 */
@JvmOverloads
fun View.preventMultipointNo(
    owner: LifecycleOwner,
    lifecycle: Lifecycle.Event? = Lifecycle.Event.ON_DESTROY
): ObservableSubscribeProxy<Unit> {
    return this.clicks()
        .autoDispose2MainE(owner, lifecycle)
}

/**
 * 防止多点 线程io转主线程 主要是给一些其他操作连用 如map操作符
 */
fun View.preventMultipoint(): Observable<Unit> {
    return this.clicks()
        .throttleFirst(2, TimeUnit.SECONDS)
}

/**
 * 防止多点 线程io转主线程 并绑定生命周期
 */
@JvmOverloads
fun View.preventMultipoint(
    owner: LifecycleOwner,
    lifecycle: Lifecycle.Event? = Lifecycle.Event.ON_DESTROY
): ObservableSubscribeProxy<Unit> {
    return this.clicks()
        .throttleFirst(2, TimeUnit.SECONDS)
        .autoDispose2MainE(owner, lifecycle)
}

/**
 * 主线程 并绑定生命周期
 */
fun <T : Any> Observable<T>.autoDispose2MainE(
    owner: LifecycleOwner,
    lifecycle: Lifecycle.Event? = Lifecycle.Event.ON_DESTROY
): ObservableSubscribeProxy<T> {
    val androidLifecycleScopeProvider = if (lifecycle == null) {
        AndroidLifecycleScopeProvider.from(owner)
    } else {
        AndroidLifecycleScopeProvider.from(owner, lifecycle)
    }
    return this.other2Main().autoDispose(androidLifecycleScopeProvider)
}

/**
 * io 主线程 并绑定生命周期
 */
fun <T : Any> Observable<T>.autoDisposeIo2MainE(
    owner: LifecycleOwner,
    lifecycle: Lifecycle.Event? = Lifecycle.Event.ON_DESTROY
): ObservableSubscribeProxy<T> {
    val androidLifecycleScopeProvider = if (lifecycle == null) {
        AndroidLifecycleScopeProvider.from(owner)
    } else {
        AndroidLifecycleScopeProvider.from(owner, lifecycle)
    }
    return this.io2Main().autoDispose(androidLifecycleScopeProvider)
}

/**
 * io转主线程
 */
fun <T : Any> Observable<T>.io2Main(): Observable<T> {
    return this.subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
}

/**
 * 其他线程转主线程
 */
fun <T : Any> Observable<T>.other2Main(): Observable<T> {
    return this.observeOn(AndroidSchedulers.mainThread())
}

/**
 * 主线程 并绑定生命周期
 */
fun <T : Any> Maybe<T>.autoDispose2MainE(
    owner: LifecycleOwner,
    lifecycle: Lifecycle.Event? = Lifecycle.Event.ON_DESTROY
): MaybeSubscribeProxy<T> {
    val androidLifecycleScopeProvider = if (lifecycle == null) {
        AndroidLifecycleScopeProvider.from(owner)
    } else {
        AndroidLifecycleScopeProvider.from(owner, lifecycle)
    }
    return this.other2Main().autoDispose(androidLifecycleScopeProvider)
}

/**
 * io 主线程 并绑定生命周期
 */
fun <T : Any> Maybe<T>.autoDisposeIo2MainE(
    owner: LifecycleOwner,
    lifecycle: Lifecycle.Event? = Lifecycle.Event.ON_DESTROY
): MaybeSubscribeProxy<T> {
    val androidLifecycleScopeProvider = if (lifecycle == null) {
        AndroidLifecycleScopeProvider.from(owner)
    } else {
        AndroidLifecycleScopeProvider.from(owner, lifecycle)
    }
    return this.io2Main().autoDispose(androidLifecycleScopeProvider)
}

/**
 * io转主线程
 */
fun <T : Any> Maybe<T>.io2Main(): Maybe<T> {
    return this.subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
}

/**
 * 其他线程转主线程
 */
fun <T : Any> Maybe<T>.other2Main(): Maybe<T> {
    return this.observeOn(AndroidSchedulers.mainThread())
}