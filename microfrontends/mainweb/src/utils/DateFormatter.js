class DateFormatter {

    static format = (dateString) => {
        const date = new Date(dateString);
        const zeroMonth = date.getMonth() < 10 - 1 ? '0' : '';
        const zeroDate = date.getDate() < 10 ? '0' : '';
        const zeroHours = date.getHours() < 10 ? '0' : '';
        const zeroMinutes = date.getMinutes() < 10 ? '0' : '';
        return `${date.getFullYear()}/${zeroMonth}${date.getMonth() + 1}/${zeroDate}${date.getDate()} ${zeroHours}${date.getHours()}:${zeroMinutes}${date.getMinutes()}`
    }
    
}

export default DateFormatter;