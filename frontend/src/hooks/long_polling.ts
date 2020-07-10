import { Observable, Subscriber, Subject, merge } from "rxjs";
import { useEffect, useState, useCallback, useMemo } from "react";

export type Message = { id: string; type: string; content: string };

class PollingChannel {
  private id: string | null = null;
  private end = false;
  public subject = new Subject<Message>();

  public readonly observable = new Observable((subscriber: Subscriber<Message>) => {
    (async () => {
      while (!this.end) {
        const message: Message = { id: this.id ?? "none", type: "resubscribe", content: "" };

        const response = await fetch("/api/game", {
          method: "POST",
          body: JSON.stringify(message), // TODO:
        });
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
          const message = (await response.json()) as Message;
          this.id = message.id;
          subscriber.next(message);
          console.log("ok");
          continue;
        }
      }
    })();
  });

  public async push(type: string, content: string) {
    console.log("pushed", type, content);
    const message: Message = { id: this.id ?? "none", type, content };
    const res = await fetch("/api/game", {
      method: "POST",
      body: JSON.stringify(message), // TODO:
    });
    const response = (await res.json()) as Message;
    this.id = response.id;
    this.subject.next(response);
  }
}

export const useLongPollingObservable = (
  initialValue?: Message | undefined,
  onChange?: (message: Message) => void
): [Message | undefined, (type: string, content: string) => void] => {
  const chan = useMemo(() => new PollingChannel(), []);
  const [value, setValue] = useState(initialValue);
  const handleChange = useCallback(
    (m: Message) => {
      setValue(m);
      onChange && onChange(m);
    },
    [onChange]
  );
  useEffect(() => {
    const subscription = merge(chan.observable, chan.subject).subscribe(handleChange);
    return () => subscription.unsubscribe();
  }, [handleChange, chan.observable, onChange]);
  const push = useCallback(
    (type: string, content: string) => {
      chan.push(type, content);
    },
    [chan]
  );
  return [value, push];
};
