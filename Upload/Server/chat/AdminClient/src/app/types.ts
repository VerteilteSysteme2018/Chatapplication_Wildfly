export class Count {
  id?: number;
  counting?: number;
  username?: string;
}

export class Trace {
  id?: number;
  username?: string;
  clientthread?: string;
  message?: string;
  serverthread?: string;
}

export class ChatUser {
  name?: string
}

export class CountResponse {
  data: CountList;
  error: any;
}

export class TraceResponse {
  data: TraceList;
  error: any;
}

export class ChatUserResponse {
  data: ChatUserList;
  error: any;
}

export class CountList {
  allCount: Count[]
}

export class TraceList {
  allTrace: Trace[]
}

export class ChatUserList{
  allChatUsers: ChatUser[]
}

export class Config
{
  ip: string = 'localhost';
  port: string = '8080';
}
