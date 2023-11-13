    
export const handleText = ( { value, maxLength } ) => value.slice(0, maxLength);

export const numberToString = ( number, valueDefault ) => ( (number && number !== undefined) ? number.toString() : valueDefault );

export const upperFirstChar = word => word[0].toUpperCase() + word.substring(1).toLowerCase();

export const upperAllFirtsChars = word => {
    const words = word.split(" ");
    for (let i = 0; i < words.length; i++) {
    words[i] = words[i][0].toUpperCase() + words[i].substr(1);
    }
    words.join(" ");
    return wors;
}