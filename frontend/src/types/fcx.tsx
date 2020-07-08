// eslint-disable-next-line @typescript-eslint/no-unused-vars
import _ from "react";
declare module "react" {
  type FCX<P = unknown> = FunctionComponent<P & { className?: string }>;
}
