const SGmail = require("@sendgrid/mail");

SGmail.setApiKey(process.env.SENDGRID_API_Key);

const sendEmail = (options) => {
  SGmail.send(options)
    .then(() => {
      console.log("Email sent");
    })
    .catch((error) => {
      console.error(error);
    });
};

module.exports = sendEmail;
