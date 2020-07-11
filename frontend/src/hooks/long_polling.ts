import { Observable, Subscriber } from "rxjs";
import { useEffect, useState, useCallback, useMemo } from "react";

export type Message = { id: string; type: string; content: string };

class PollingChannel {
  private id: string | null = null;
  private end = false;
  private channel: Message[] = [];

  public readonly observable = new Observable((subscriber: Subscriber<Message>) => {
    (async () => {
      console.log("new");
      while (!this.end) {
        const message: Message = this.channel.shift() ?? { id: this.id ?? "none", type: "resubscribe", content: "" };

        const response = await fetch("/api/game", {
          method: "POST",
          body: JSON.stringify(message), // TODO:
        });
        if (response.status === 502) {
          // 接続タイムアウトエラー
          // 接続が長時間保留されていて、リモートサーバやプロキシがそれを閉じたときに発生する場合があります
          // 再接続しましょう
        } else if (response.status !== 200) {
          // エラーを表示
          console.error(response.statusText);
          subscriber.error(response.statusText);
          // 1秒後に再接続します
          await new Promise((resolve) => setTimeout(resolve, 1000));
        } else {
          // メッセージを取得しました
          const message = (await response.json()) as Message;
          this.id = message.id;
          if (message.type === "wait_input") {
            while (this.channel.length === 0) {
              // 100msごとに確認
              await new Promise((resolve) => setTimeout(resolve, 100));
            }
          } else {
            subscriber.next(message);
          }
        }
        continue;
      }
    })();
  });

  public async push(type: string, content: string) {
    const message: Message = { id: this.id ?? "none", type, content };
    this.channel.push(message);
  }
}

export const useLongPollingObservable = (
  onChange?: (message: Message) => void
): [Message | undefined, (type: string, content: string) => void] => {
  const chan = useMemo(() => new PollingChannel(), []);
  const [value, setValue] = useState<Message | undefined>(undefined);
  const handleChange = useCallback(
    (m: Message) => {
      setValue(m);
      onChange && onChange(m);
    },
    [onChange]
  );
  useEffect(() => {
    const subscription = chan.observable.subscribe(handleChange);
    return () => subscription.unsubscribe();
  }, [handleChange, chan.observable, onChange]);
  const push = useCallback(
    (type: string, content: string) => {
      chan.push(type, content);
      console.log(type, content);
    },
    [chan]
  );
  return [value, push];
};
