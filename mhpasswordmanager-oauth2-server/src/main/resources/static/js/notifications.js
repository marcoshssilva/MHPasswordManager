const template = `
<div id="{{id}}" class="toast" role="alert" aria-live="assertive" aria-atomic="true" data-bs-delay="5000">

<div class="toast-header text-bg-danger">
  <strong class="me-auto"> {{title}} </strong>
  <small> {{time}} </small>
  <button type="button" class="btn-close" data-bs-dismiss="toast" aria-label="Close"></button>
</div>

<div class="toast-body">
  {{content}}
</div>

</div>

`;

function createNotification(title, content, time) {
    let elementNotifications = document.getElementById("notifications");
    let id = "notification_" + randomId();
    let notificationElement = template.replace("{{id}}", id).replace("{{title}}", title).replace("{{content}}", content).replace("{{time}}", time);
    elementNotifications.innerHTML += notificationElement;

    let notification = new bootstrap.Toast(document.getElementById(id))
    notification.show();

    return notification;
}

function randomId() {
    return Math.random() % 999999;
}