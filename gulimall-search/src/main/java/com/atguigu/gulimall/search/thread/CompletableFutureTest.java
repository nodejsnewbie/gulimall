package com.atguigu.gulimall.search.thread;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Supplier;

public class CompletableFutureTest {

    public static ExecutorService executorService = Executors.newFixedThreadPool(10);

    public static void main(String[] args) {
        System.out.println("Main-in-当前线程:" + Thread.currentThread().getName());
        test15();
        System.out.println("Main-out-当前线程:" + Thread.currentThread().getName());
    }

    /**
     * 不需要返回值
     */
    private static void test1() {
        System.out.println("test1");
        CompletableFuture.runAsync(new Runnable() {
            @Override
            public void run() {
                System.out.println("当前线程:" + Thread.currentThread().getName());
                int a = 10 / 2;
                System.out.println("运行结果:" + a);
            }
        }, executorService);
    }

    /**
     * 得到返回值
     */
    private static void test2() {
        System.out.println("test2");
        CompletableFuture<Integer> completableFuture = CompletableFuture.supplyAsync(new Supplier<Integer>() {
            @Override
            public Integer get() {
                System.out.println("当前线程:" + Thread.currentThread().getName());
                int a = 10 / 2;
                System.out.println("运行结果:" + a);
                return a;
            }
        }, executorService);
        try {
            Integer integer = completableFuture.get();
            System.out.println("最终结果:" + integer);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 感知异常并且修改返回值
     */
    private static void test3() {
        System.out.println("test3");
        CompletableFuture<Integer> completableFuture = CompletableFuture.supplyAsync(new Supplier<Integer>() {
            @Override
            public Integer get() {
                System.out.println("当前线程:" + Thread.currentThread().getName());
                int a = 10 / 0;
                System.out.println("运行结果:" + a);
                return a;
            }
        }, executorService).whenComplete((res, exception) -> {
            System.out.println("异步任务完成了，结果:" + res + "--异常:" + exception);
        }).exceptionally(exception -> {
            //当异常的时候使用该返回值
            return 10;
        });
        try {
            Integer integer = completableFuture.get();
            System.out.println("最终结果:" + integer);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 处理返回结果
     */
    private static void test4() {
        System.out.println("test4");
        CompletableFuture<Integer> completableFuture = CompletableFuture.supplyAsync(new Supplier<Integer>() {
            @Override
            public Integer get() {
                System.out.println("当前线程:" + Thread.currentThread().getName());
                int a = 10 / 2;
                System.out.println("运行结果:" + a);
                return a;
            }
        }, executorService).handle((res, thr) -> {
            if (res != null) {
                return res * 2;
            }
            return 0;
        });
        try {
            Integer integer = completableFuture.get();
            System.out.println("最终结果:" + integer);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 线程串行
     */
    private static void test5() {
        System.out.println("test5");
        CompletableFuture.supplyAsync(new Supplier<Integer>() {
            @Override
            public Integer get() {
                System.out.println("当前线程:" + Thread.currentThread().getName());
                int a = 10 / 2;
                System.out.println("运行结果:" + a);
                return a;
            }
        }, executorService).thenRunAsync(() -> {
            System.out.println("任务2启动了");
        }, executorService);
    }

    /**
     * 线程串行得到第一步任务的结果
     */
    private static void test6() {
        System.out.println("test6");
        CompletableFuture.supplyAsync(new Supplier<Integer>() {
            @Override
            public Integer get() {
                System.out.println("当前线程:" + Thread.currentThread().getName());
                int a = 10 / 2;
                System.out.println("运行结果:" + a);
                return a;
            }
        }, executorService).thenAcceptAsync(res -> {
            System.out.println("任务2启动了:");
            System.out.println("第一步的结果是:" + res);
        }, executorService);
    }

    /**
     * 线程串行得到第一步任务的结果并且把
     * 第二步的结果返回
     */
    private static void test7() {
        System.out.println("test7");
        CompletableFuture<Integer> completableFuture = CompletableFuture.supplyAsync(new Supplier<Integer>() {
            @Override
            public Integer get() {
                System.out.println("当前线程:" + Thread.currentThread().getName());
                int a = 10 / 2;
                System.out.println("运行结果:" + a);
                return a;
            }
        }, executorService).thenApplyAsync(res -> {
            System.out.println("任务2启动了:");
            System.out.println("第一步的结果是:" + res);
            return res * 2;
        }, executorService);

        try {
            Integer integer = completableFuture.get();
            System.out.println("最终结果:" + integer);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 两个任务都完成之后触发任务3
     */
    private static void test8() {
        System.out.println("test8");
        CompletableFuture<Integer> future1 = CompletableFuture.supplyAsync(new Supplier<Integer>() {
            @Override
            public Integer get() {
                System.out.println("future1当前线程:" + Thread.currentThread().getName());
                int a = 10 / 2;
                System.out.println("运行结果:" + a);
                return a;
            }
        });
        CompletableFuture<String> future2 = CompletableFuture.supplyAsync(new Supplier<String>() {
            @Override
            public String get() {
                System.out.println("future2当前线程:" + Thread.currentThread().getName());
                return "hello";
            }
        });
        future1.runAfterBothAsync(future2, () -> {
            System.out.println("任务3开始" + Thread.currentThread().getName());
        }, executorService);
    }

    /**
     * 两个任务都完成之后触发任务3,再任务3中拿到任务1和任务2的结果
     * 任务3没返回值
     */
    private static void test9() {
        System.out.println("test9");
        CompletableFuture<Integer> future1 = CompletableFuture.supplyAsync(new Supplier<Integer>() {
            @Override
            public Integer get() {
                System.out.println("future1当前线程:" + Thread.currentThread().getName());
                int a = 10 / 2;
                System.out.println("运行结果:" + a);
                return a;
            }
        });
        CompletableFuture<String> future2 = CompletableFuture.supplyAsync(new Supplier<String>() {
            @Override
            public String get() {
                System.out.println("future2当前线程:" + Thread.currentThread().getName());
                return "hello";
            }
        });
        future1.thenAcceptBothAsync(future2, (f1, f2) -> {
            System.out.println("任务3开始" + Thread.currentThread().getName());
            System.out.println("任务1结果:" + f1);
            System.out.println("任务2结果:" + f2);
        }, executorService);
    }

    /**
     * 两个任务都完成之后触发任务3,再任务3中拿到任务1和任务2的结果
     * 任务3有返回值
     */
    private static void test10() {
        System.out.println("test10");
        CompletableFuture<Integer> future1 = CompletableFuture.supplyAsync(new Supplier<Integer>() {
            @Override
            public Integer get() {
                System.out.println("future1当前线程:" + Thread.currentThread().getName());
                int a = 10 / 2;
                System.out.println("运行结果:" + a);
                return a;
            }
        });
        CompletableFuture<String> future2 = CompletableFuture.supplyAsync(new Supplier<String>() {
            @Override
            public String get() {
                System.out.println("future2当前线程:" + Thread.currentThread().getName());
                return "hello";
            }
        });
        CompletableFuture<String> completableFuture = future1.thenCombineAsync(future2, (f1, f2) -> {
            System.out.println("任务3开始" + Thread.currentThread().getName());
            System.out.println("任务1结果:" + f1);
            System.out.println("任务2结果:" + f2);
            return f1 + f2;
        }, executorService);

        try {
            String integer = completableFuture.get();
            System.out.println("最终结果:" + integer);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    /**
     * 两个任务中有一个任务完成了，就触发任务3
     */
    private static void test11() {
        System.out.println("test11");
        CompletableFuture<Integer> future1 = CompletableFuture.supplyAsync(new Supplier<Integer>() {
            @Override
            public Integer get() {
                System.out.println("future1当前线程:" + Thread.currentThread().getName());
                int a = 10 / 2;
                System.out.println("运行结果:" + a);
                return a;
            }
        });
        CompletableFuture<String> future2 = CompletableFuture.supplyAsync(new Supplier<String>() {
            @Override
            public String get() {
                System.out.println("future2当前线程:" + Thread.currentThread().getName());
                return "hello";
            }
        });
        future1.runAfterEitherAsync(future2, () -> {
            System.out.println("任务3开始" + Thread.currentThread().getName());
        }, executorService);
    }

    /**
     * 两个任务中有一个任务完成了，就触发任务3
     * 触发任务3的时候，得到完成任务的结果
     */
    private static void test12() {
        System.out.println("test12");
        CompletableFuture<Object> future1 = CompletableFuture.supplyAsync(new Supplier<Object>() {
            @Override
            public Object get() {
                System.out.println("future1当前线程:" + Thread.currentThread().getName());
                int a = 10 / 2;
                System.out.println("运行结果:" + a);
                return a;
            }
        });
        CompletableFuture<Object> future2 = CompletableFuture.supplyAsync(new Supplier<Object>() {
            @Override
            public Object get() {
                System.out.println("future2当前线程:" + Thread.currentThread().getName());
                return "hello";
            }
        });
        future1.acceptEitherAsync(future2, (res) -> {
            System.out.println("任务3开始" + Thread.currentThread().getName());
            System.out.println("上一次结果:" + res);
        }, executorService);
    }

    /**
     * 两个任务中有一个任务完成了，就触发任务3
     * 触发任务3的时候，得到完成任务的结果，并且返回结果
     */
    private static void test13() {
        System.out.println("test13");
        CompletableFuture<Object> future1 = CompletableFuture.supplyAsync(new Supplier<Object>() {
            @Override
            public Object get() {
                System.out.println("future1当前线程:" + Thread.currentThread().getName());
                int a = 10 / 2;
                System.out.println("运行结果:" + a);
                return a;
            }
        });
        CompletableFuture<Object> future2 = CompletableFuture.supplyAsync(new Supplier<Object>() {
            @Override
            public Object get() {
                System.out.println("future2当前线程:" + Thread.currentThread().getName());
                return "hello";
            }
        });
        CompletableFuture<String> completableFuture = future1.applyToEitherAsync(future2, (res) -> {
            System.out.println("任务3开始" + Thread.currentThread().getName());
            System.out.println("上一次结果:" + res);
            return res + "哈哈";
        }, executorService);

        try {
            String integer = completableFuture.get();
            System.out.println("最终结果:" + integer);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 所有任务都完成了。最后得到结果
     */
    private static void test14() {
        System.out.println("test14");
        CompletableFuture<Object> future1 = CompletableFuture.supplyAsync(new Supplier<Object>() {
            @Override
            public Object get() {
                try {
                    Thread.sleep(3000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                System.out.println("查询商品图片信息");
                return "abc.jpg";
            }
        }, executorService);
        CompletableFuture<Object> future2 = CompletableFuture.supplyAsync(new Supplier<Object>() {
            @Override
            public Object get() {
                System.out.println("查询商品的属性");
                return "黑色+256G";
            }
        }, executorService);
        CompletableFuture<Object> future3 = CompletableFuture.supplyAsync(new Supplier<Object>() {
            @Override
            public Object get() {
                System.out.println("查询商品的介绍");
                return "华为";
            }
        }, executorService);

        CompletableFuture<Void> voidCompletableFuture = CompletableFuture.allOf(future1, future2, future3);
        try {
            voidCompletableFuture.get();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
    }

    /**
     * 有一个任务执行成功。最后得到结果
     */
    private static void test15() {
        System.out.println("test15");
        CompletableFuture<Object> future1 = CompletableFuture.supplyAsync(new Supplier<Object>() {
            @Override
            public Object get() {
                try {
                    Thread.sleep(3000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                System.out.println("查询商品图片信息");
                return "abc.jpg";
            }
        }, executorService);
        CompletableFuture<Object> future2 = CompletableFuture.supplyAsync(new Supplier<Object>() {
            @Override
            public Object get() {
                System.out.println("查询商品的属性");
                return "黑色+256G";
            }
        }, executorService);
        CompletableFuture<Object> future3 = CompletableFuture.supplyAsync(new Supplier<Object>() {
            @Override
            public Object get() {
                System.out.println("查询商品的介绍");
                return "华为";
            }
        }, executorService);

        CompletableFuture<Object> objectCompletableFuture = CompletableFuture.anyOf(future1, future2, future3);
        try {
            Object o = objectCompletableFuture.get();
            System.out.println("最终结果:" + o);
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
    }

}
