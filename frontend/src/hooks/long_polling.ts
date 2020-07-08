import { Observable, Subscriber } from "rxjs";
import { useEffect, useState } from "react";

const getLongPoolingObservable = <T>() =>
  new Observable((subscriber: Subscriber<T>) => {
    (async () => {
      const end = false;
      while (!end) {
        const response = await fetch("/subscribe");
        console.log("sub");
        if (response.status === 502) {
          // 接続タイムアウトエラー
          // 接続が長時間保留されていて、リモートサーバやプロキシがそれを閉じたときに発生する場合があります
          // 再接続しましょう
          continue;
        } else if (response.status !== 200) {
          // エラーを表示
          console.error(response.statusText);
          subscriber.error(response.statusText);
          // 1秒後に再接続します
          await new Promise((resolve) => setTimeout(resolve, 1000));
          continue;
        } else {
          // メッセージを取得しました
          const message = ((await response.text()) as any) as T;
          subscriber.next(message);
          console.log("ok");
          continue;
        }
      }
    })();
  });

export const useLongPollingObservable = (initialValue?: string | undefined): string | null => {
  const [value, setValue] = useState(initialValue ?? null);
  useEffect(() => {
    const s = (n: string) => setValue(n);
    const subscription = getLongPoolingObservable<string>().subscribe(s);
    return () => subscription.unsubscribe();
  }, []);
  return value;
};
