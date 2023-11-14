    
export const handleText = ( { value, maxLength } ) => value.slice(0, maxLength);

export const numberToString = ( number, valueDefault ) => ( (number && number !== undefined) ? number.toString() : valueDefault );

export const upperFirstChar = word => word[0].toUpperCase() + word.substring(1).toLowerCase();

export const upperAllFirtsChars = word => {
    const words = word.split(" ");
    for (let i = 0; i < words.length; i++) {
    words[i] = words[i][0].toUpperCase() + words[i].substr(1);
    }
    words.join(" ");
    return words;
}

export const handleDate = ( date, currentDate ) => {
    const invalidDate = ( date === undefined || date === null || date === '');
    if ( invalidDate ) {
        return currentDate ? new Date() : null;
    }
    return new Date(date);
}

export const handleDateStr = ( strDate, currentDate ) => {
    const invalidDate = ( strDate === undefined || strDate === null || strDate === '');
    if ( invalidDate ) {
        return currentDate ? new Date() : null;
    }
    const dateCreated = convertDateToUTC(strDate);
    return dateCreated;
}

const convertDateToUTC = ( strDate ) => {
    const dateParts = strDate.split("/");
    return new Date(+dateParts[2], dateParts[1] - 1, +dateParts[0]); 
};