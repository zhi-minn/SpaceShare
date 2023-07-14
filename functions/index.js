const functions = require('firebase-functions');
const admin = require('firebase-admin');
admin.initializeApp()

exports.checkUpcomingReservations = functions.pubsub
    .schedule('every day 00:00')
    .timeZone('EST')
    .onRun(async (context) => {
        try {
            const currentTime = new Date();
            const next24Hours = new Date();
            next24Hours.setHours(next24Hours.getHours() + 24);

            const upcomingReservations = await admin
                .firestore()
                .collection("reservations")
                .where('startDate', '>=', currentTime)
                .where('startDate', '<=', next24Hours)
                .get()

            upcomingReservations.forEach((reservation) => {
                const { clientId, hostId, startDate } = reservation.data()
                sendNotificationToUser(clientId, startDate)
                sendNotificationToUser(hostId, startDate)
            })
            console.log("Scheduled job completed successfully")
        } catch (error) {
            console.error("Scheduled job error: ", error)
            return null
        }
    }) 

function sendNotificationToUser(userId, startDate) {
    console.log(userId)
    const date = startDate.toDate()
    const formattedDate = date.toLocaleDateString()
    const options = { timeZone: 'America/New_York' };
    const formattedTime = date.toLocaleTimeString('en-US', options)
    const userRef = admin.firestore().collection("users").doc(userId)
    userRef.get().then((userDoc) => {
        const userData = userDoc.data()
        console.log(userData)
        const fcmToken = userData.fcmToken;

        if (fcmToken) {
            const message = {
                token: fcmToken,
                notification: {
                    title: 'Upcoming Reservation',
                    body: 'You have an upcoming reservation on ' + formattedDate + ' at ' + formattedTime
                }
            }

            admin.messaging().send(message)
                .then((response) => {
                    console.log("Notification sent successfully:" , response);
                })
                .catch((error) => {
                    console.error("Error sending notification:", error)
                })
        }
    })
}