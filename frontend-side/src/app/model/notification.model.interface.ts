export class NotificationModelInterface {
  constructor(message: string, isErrorMessage: boolean) {
    this.message = message;
    this.isErrorMessage = isErrorMessage;
  }

  message: string;
  isErrorMessage: boolean;
}
